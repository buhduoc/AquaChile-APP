package com.duoc.aquachileapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aquachileapp.data.Remote.toDto
import com.duoc.aquachileapp.data.Remote.toEvidenciaEquipo
import com.duoc.aquachileapp.data.Remote.toPreChequeo
import com.duoc.aquachileapp.data.local.AppDatabase
import com.duoc.aquachileapp.data.local.toDomain
import com.duoc.aquachileapp.data.local.toEntity
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.EvidenciaEquipo
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.repository.EvidenciaEquipoRepository
import com.duoc.aquachileapp.sync.SyncManager
import com.duoc.aquachileapp.repository.PreChequeoRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistorialViewModel(application: Application) : AndroidViewModel(application) {



    // ROOM
    private val dao =
        AppDatabase
            .obtenerInstancia(application)
            .preChequeoDao()


    // API / SPRING BOOT

    private val repository = PreChequeoRepository()
    private val evidenciaRepository = EvidenciaEquipoRepository()




    // ESTADOS


    val registros =
        mutableStateListOf<PreChequeo>()

    private val estadosRoom = androidx.compose.runtime.mutableStateMapOf<Int, String>()

    fun estadoPorId(id: Int): String? = estadosRoom[id]


    val evidenciasServidor = mutableStateListOf<EvidenciaEquipo>()

    var evidenciasConsultadas by mutableStateOf(false)
        private set
    var cargandoServidor by mutableStateOf(false)
        private set


    var errorServidor by mutableStateOf<String?>(null)
        private set


    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")

    // INICIO

    init {

        // Observar Room continuamente
        viewModelScope.launch {

            dao.obtenerTodos()
                .collectLatest { entities ->

                    estadosRoom.clear()
                    entities.forEach { estadosRoom[it.id] = it.estadoSincronizacion }
                    registros.clear()

                    registros.addAll(
                        entities.map {
                            it.toDomain()
                        }
                    )
                }
        }


        // Descarga automatica pausada hasta implementar una fusion segura
        // entre IDs locales y IDs del servidor.
        // cargarDesdeServidor()

        // Reintenta los registros pendientes al abrir el historial.
        SyncManager.programar(application)
    }


    // GET - SPRING BOOT → ROOM → ANDROID

    fun cargarDesdeServidor() {

        viewModelScope.launch {

            cargandoServidor = true

            try {

                val preChequeosRemotos =
                    repository.listar()


                preChequeosRemotos.forEach { dto ->

                    val preChequeo =
                        dto.toPreChequeo()


                    dao.insertar(
                        preChequeo.toEntity()
                    )
                }


                errorServidor = null


                Log.d(
                    "API_PRECHEQUEO",
                    "PreChequeos cargados desde servidor: ${preChequeosRemotos.size}"
                )


            } catch (e: Exception) {

                /*
                 * Si falla Internet,
                 * Room sigue conservando
                 * el historial local.
                 */

                errorServidor =
                    "No se pudo actualizar desde el servidor"


                Log.e(
                    "API_PRECHEQUEO",
                    "Error GET preChequeos: ${e.message}"
                )


            } finally {

                cargandoServidor = false
            }
        }
    }

    fun guardar(
        items: List<ItemChecklist>,
        supervisor: String,
        observaciones: String = "",
        fotos: List<String> = emptyList()
    ) {

        val fecha = LocalDateTime.now()
            .format(
                DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy HH:mm"
                )
            )

        val nuevoPreChequeo = PreChequeo(

            id = 0,

            centro = centro.ifBlank {
                "Centro Puerto Montt"
            },

            fecha = fecha,

            buzo = buzo.ifBlank {
                "Sin buzo"
            },

            supervisor = supervisor,

            items = items,

            observaciones = observaciones,

            fotos = fotos,

            sistolica = 120,
            diastolica = 80,
            saturacion = 98,

            alertas = emptyList(),

            // Valores normales por defecto
            edad = 35,
            pesoKg = 72.0,
            alturaCm = 175.0,

            examenPreocupacionalVigente = true,
            testEsfuerzoVigente = true,

            puntajeEscuelaCalbuco = 90
        )


        // IMPORTANTE:
        // usamos agregar() y no dao.insertar()
        // para guardar primero en Room.
        agregar(nuevoPreChequeo)


        centro = ""
        buzo = ""
    }


    // GUARDADO OFFLINE-FIRST: ANDROID -> ROOM
    // El envio al backend se implementara despues con WorkManager.
    fun agregar(preChequeo: PreChequeo) {
        viewModelScope.launch {
            try {
                val entidad = preChequeo
                    .copy(id = 0)
                    .toEntity()
                    .copy(
                        estadoSincronizacion = "PENDIENTE",
                        idServidor = null,
                        ultimoErrorSincronizacion = null
                    )

                val idLocal = dao.insertar(entidad)
                // Solo programar despues de confirmar el guardado en Room.
                SyncManager.programar(getApplication())
                errorServidor = null

                Log.d(
                    "OFFLINE_SYNC",
                    "Guardado local ID=$idLocal UUID=${entidad.uuid}"
                )
            } catch (e: Exception) {
                errorServidor = "No se pudo guardar el prechequeo localmente"
                Log.e("OFFLINE_SYNC", "Error guardando en Room", e)
            }
        }
    }



    // BUSCAR PRE-CHEQUEO

    fun buscar(
        id: Int
    ): PreChequeo? {

        return registros.firstOrNull {
            it.id == id
        }
    }

    //ACTUALIZAR PRE_CHEQUEO

    fun actualizar(
        preChequeo: PreChequeo
    ) {

        viewModelScope.launch {

            // Debe tener un ID real del servidor
            if (preChequeo.id <= 0) {

                Log.e(
                    "API_PRECHEQUEO",
                    "No se puede actualizar un PreChequeo sin ID válido"
                )

                return@launch
            }


            try {

                // =====================================
                // 1. ACTUALIZAR EN SPRING BOOT
                // =====================================

                val respuesta =
                    repository.actualizar(
                        preChequeo.id.toLong(),
                        preChequeo.toDto()
                    )


                // =====================================
                // 2. ACTUALIZAR ROOM
                // =====================================

                val actualizado =
                    respuesta.toPreChequeo()


                dao.insertar(
                    actualizado.toEntity()
                )


                Log.d(
                    "API_PRECHEQUEO",
                    "PreChequeo actualizado correctamente. ID: ${respuesta.id}"
                )


            } catch (e: Exception) {

                Log.e(
                    "API_PRECHEQUEO",
                    "Error al actualizar PreChequeo: ${e.message}"
                )
            }
        }
    }

    //ELIMINAR PRE_CHEQUEO

    fun eliminar(id: Int) {
        viewModelScope.launch {
            try {
                errorServidor = null
                val entidad = dao.obtenerPorId(id)
                if (entidad == null) {
                    errorServidor = "Pre-chequeo no encontrado"
                    return@launch
                }
                val idServidor = entidad.idServidor
                if (entidad.estadoSincronizacion != "SINCRONIZADO" || idServidor == null) {
                    errorServidor = "El pre-chequeo debe sincronizarse antes de eliminarlo"
                    return@launch
                }
                Log.d("API_PRECHEQUEO", "Eliminando ID local=$id, ID servidor=$idServidor")
                repository.eliminar(idServidor)
                dao.eliminarPorId(id)
                Log.d("API_PRECHEQUEO", "Pre-chequeo eliminado correctamente")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: retrofit2.HttpException) {
                errorServidor = "Error al eliminar: HTTP ${e.code()}"
                Log.e("API_PRECHEQUEO", "Error HTTP al eliminar: ${e.code()}", e)
            } catch (e: Exception) {
                errorServidor = "No se pudo eliminar el pre-chequeo"
                Log.e("API_PRECHEQUEO", "Error al eliminar pre-chequeo", e)
            }
        }
    }

    // DELETE - RECIBIENDO OBJETO


    fun eliminar(
        preChequeo: PreChequeo
    ) {

        eliminar(
            preChequeo.id
        )
    }


    //GET DE ID FAENA
    fun cargarEvidencias(
        preChequeoId: Int
    ) {

        viewModelScope.launch {

            // Limpiar evidencias del detalle anterior
            evidenciasServidor.clear()

            evidenciasConsultadas = false

            try {

                val respuesta =
                    evidenciaRepository.listarPorFaena(
                        preChequeoId.toString()
                    )

                evidenciasServidor.addAll(
                    respuesta.map {
                        it.toEvidenciaEquipo()
                    }
                )

                evidenciasConsultadas = true

                Log.d(
                    "API_EVIDENCIA",
                    "Evidencias cargadas para faena $preChequeoId: ${respuesta.size}"
                )

            } catch (e: Exception) {

                evidenciasConsultadas = false

                Log.e(
                    "API_EVIDENCIA",
                    "Error cargando evidencias: ${e.message}"
                )
            }
        }
    }

    //ACTUALIZAR EVIDENCIAS EQUIPO

    fun actualizarEvidencia(
        evidencia: EvidenciaEquipo
    ) {

        val id = evidencia.id

        if (id == null) {

            Log.e(
                "API_EVIDENCIA",
                "No se puede actualizar una evidencia sin ID"
            )

            return
        }


        viewModelScope.launch {

            try {

                val idFaena =
                    evidencia.idFaena ?: ""


                val respuesta =
                    evidenciaRepository.actualizar(

                        id,

                        evidencia.toDto(
                            idFaenaServidor = idFaena,

                            responsableDefecto =
                                evidencia.responsable
                                    ?: "Sin responsable"
                        )
                    )


                val actualizada =
                    respuesta.toEvidenciaEquipo()


                val posicion =
                    evidenciasServidor.indexOfFirst {
                        it.id == id
                    }


                if (posicion >= 0) {

                    evidenciasServidor[posicion] =
                        actualizada
                }


                Log.d(
                    "API_EVIDENCIA",
                    "Evidencia actualizada correctamente. ID: $id"
                )


            } catch (e: Exception) {

                Log.e(
                    "API_EVIDENCIA",
                    "Error actualizando evidencia: ${e.message}"
                )
            }
        }
    }

    //ELIMINAR EVIDENCIA EQUIPO

    fun eliminarEvidencia(
        evidencia: EvidenciaEquipo
    ) {

        val id = evidencia.id

        if (id == null) {

            Log.e(
                "API_EVIDENCIA",
                "No se puede eliminar una evidencia sin ID"
            )

            return
        }


        viewModelScope.launch {

            try {

                evidenciaRepository.eliminar(id)


                evidenciasServidor.removeAll {
                    it.id == id
                }


                Log.d(
                    "API_EVIDENCIA",
                    "Evidencia eliminada correctamente. ID: $id"
                )


            } catch (e: retrofit2.HttpException) {

                if (e.code() == 404) {

                    evidenciasServidor.removeAll {
                        it.id == id
                    }


                    Log.d(
                        "API_EVIDENCIA",
                        "Evidencia ya no existía en servidor. Eliminada de Android. ID: $id"
                    )

                } else {

                    Log.e(
                        "API_EVIDENCIA",
                        "Error HTTP eliminando evidencia: ${e.code()}"
                    )
                }


            } catch (e: Exception) {

                Log.e(
                    "API_EVIDENCIA",
                    "Error eliminando evidencia: ${e.message}"
                )
            }
        }
    }






}


