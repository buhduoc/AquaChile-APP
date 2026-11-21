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

                    registros.clear()

                    registros.addAll(
                        entities.map {
                            it.toDomain()
                        }
                    )
                }
        }


        // Actualizar desde Spring Boot
        cargarDesdeServidor()
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
        // para guardar en Room + Spring Boot.
        agregar(nuevoPreChequeo)


        centro = ""
        buzo = ""
    }


    // POST - ANDROID → ROOM → SPRING BOOT

    fun agregar(
        preChequeo: PreChequeo
    ) {

        viewModelScope.launch {

            // 1. Guardar primero localmente
            val idLocal =
                dao.insertar(
                    preChequeo
                        .copy(id = 0)
                        .toEntity()
                )


            try {

                //1. POST PRE-CHEQUEO
                val respuesta =
                    repository.crear(
                        preChequeo.toDto()
                    )

                // =====================================
                // 2. POST EVIDENCIAS
                // =====================================

                val idFaenaServidor =
                    respuesta.id?.toString()


                if (idFaenaServidor != null) {

                    preChequeo.evidenciasEquipos

                        // Solo mandar evidencias
                        // que realmente tienen fotografía
                        .filter {
                            !it.uriFotoActual.isNullOrBlank()
                        }

                        .forEach { evidencia ->

                            try {

                                val evidenciaGuardada =
                                    evidenciaRepository.crear(

                                        evidencia.toDto(

                                            idFaenaServidor =
                                                idFaenaServidor,

                                            responsableDefecto =
                                                preChequeo.buzo
                                        )
                                    )


                                Log.d(
                                    "API_EVIDENCIA",
                                    "Evidencia guardada: ${evidenciaGuardada.nombreEquipo} | ID: ${evidenciaGuardada.id}"
                                )


                            } catch (e: Exception) {

                                Log.e(
                                    "API_EVIDENCIA",
                                    "Error guardando ${evidencia.nombreEquipo}: ${e.message}"
                                )
                            }
                        }


                }
                // 3. Eliminar registro temporal local
                dao.eliminarPorId(
                    idLocal.toInt()
                )


                // 4. Guardar usando el ID real del servidor
                val preChequeoServidor =
                    respuesta.toPreChequeo()


                dao.insertar(
                    preChequeoServidor.toEntity()
                )


                Log.d(
                    "API_PRECHEQUEO",
                    "PreChequeo sincronizado. ID servidor: ${respuesta.id}"
                )


            } catch (e: Exception) {

                /*
                 * Si falla la API,
                 * permanece el registro local.
                 */

                Log.e(
                    "API_PRECHEQUEO",
                    "Guardado local. Pendiente de sincronizar: ${e.message}"
                )
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

    fun eliminar(
        id: Int
    ) {

        viewModelScope.launch {

            if (id <= 0) {

                Log.e(
                    "API_PRECHEQUEO",
                    "No se puede eliminar un PreChequeo sin ID válido"
                )

                return@launch
            }


            try {

                // =====================================
                // 1. ELIMINAR DEL SERVIDOR
                // =====================================

                repository.eliminar(
                    id.toLong()
                )


                // =====================================
                // 2. ELIMINAR DE ROOM
                // =====================================

                dao.eliminarPorId(id)


                Log.d(
                    "API_PRECHEQUEO",
                    "PreChequeo eliminado correctamente. ID: $id"
                )


            } catch (e: retrofit2.HttpException) {

                // Si Spring Boot devuelve 404,
                // significa que ya no existe en MySQL.
                if (e.code() == 404) {

                    dao.eliminarPorId(id)

                    Log.d(
                        "API_PRECHEQUEO",
                        "PreChequeo ya no existía en servidor. Eliminado de Room. ID: $id"
                    )

                } else {

                    Log.e(
                        "API_PRECHEQUEO",
                        "Error HTTP al eliminar: ${e.code()}"
                    )
                }



            } catch (e: Exception) {

                Log.e(
                    "API_PRECHEQUEO",
                    "Error al eliminar PreChequeo: ${e.message}"
                )
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


