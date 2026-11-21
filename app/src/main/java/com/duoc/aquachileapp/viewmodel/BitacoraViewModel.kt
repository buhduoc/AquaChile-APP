package com.duoc.aquachileapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aquachileapp.data.Remote.toBitacora
import com.duoc.aquachileapp.data.Remote.toDto
import com.duoc.aquachileapp.model.BitacoraPostInmersion
import com.duoc.aquachileapp.repository.BitacoraRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BitacoraViewModel : ViewModel() {


    // =========================================
    // REPOSITORY
    // =========================================

    private val repository =
        BitacoraRepository()


    // =========================================
    // LISTA DE BITÁCORAS
    // =========================================

    val registrosBitacora =
        mutableStateListOf<BitacoraPostInmersion>()


    // =========================================
    // ESTADOS API
    // =========================================

    var idEnEdicion by mutableStateOf<Int?>(null)
        private set

    private var fechaEnEdicion: String? = null
    var cargandoServidor by mutableStateOf(false)
        private set

    var errorServidor by mutableStateOf<String?>(null)
        private set


    // =========================================
    // CAMPOS DEL FORMULARIO
    // =========================================

    var centro by mutableStateOf("Centro Puerto Montt")
    var buzo by mutableStateOf("")
    var tiempoFondoStr by mutableStateOf("")
    var profundidadStr by mutableStateOf("")
    var velDescensoStr by mutableStateOf("")
    var velAscensoStr by mutableStateOf("")
    var observaciones by mutableStateOf("")
    var error by mutableStateOf<String?>(null)


    // =========================================
    // INICIO
    // =========================================

    init {

        cargarDesdeServidor()
    }

    //GET
    fun cargarDesdeServidor() {

        viewModelScope.launch {

            cargandoServidor = true

            try {

                val respuesta =
                    repository.listar()


                registrosBitacora.clear()


                registrosBitacora.addAll(

                    respuesta.map {
                        it.toBitacora()
                    }
                )


                errorServidor = null


                Log.d(
                    "API_BITACORA",
                    "Bitácoras cargadas desde servidor: ${respuesta.size}"
                )


            } catch (e: Exception) {

                errorServidor =
                    "No se pudieron cargar las bitácoras"


                Log.e(
                    "API_BITACORA",
                    "Error GET Bitácoras: ${e.message}"
                )


            } finally {

                cargandoServidor = false
            }
        }
    }


    fun registrar(
        preChequeoId: Int? = null,
        nombreBuzo: String? = null
    ) {

        val tFondo =
            tiempoFondoStr.toIntOrNull()

        val prof =
            profundidadStr.toDoubleOrNull()

        val vDesc =
            velDescensoStr.toDoubleOrNull()

        val vAsc =
            velAscensoStr.toDoubleOrNull()


        val buzoNom =

            if (buzo.isNotBlank()) {

                buzo

            } else {

                nombreBuzo
                    ?: "Buzo A Cargo"
            }


        // =========================================
        // VALIDACIONES ANDROID
        // =========================================

        error = when {

            tFondo == null ||
                    tFondo <= 0 ->

                "Ingresa un tiempo de fondo válido (minutos)"


            prof == null ||
                    prof <= 0 ->

                "Ingresa una profundidad máxima válida (metros)"


            vDesc == null ||
                    vDesc < 0 ->

                "Ingresa la velocidad de descenso (m/min)"


            vAsc == null ||
                    vAsc < 0 ->

                "Ingresa la velocidad de ascenso (m/min)"


            else -> null
        }


        if (
            error != null ||
            tFondo == null ||
            prof == null ||
            vDesc == null ||
            vAsc == null
        ) {

            return
        }


        val fecha =
            fechaEnEdicion
                ?: LocalDateTime.now()
                    .format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy HH:mm"
                        )
                    )


        // =========================================
        // CREAR BITÁCORA LOCAL
        // =========================================

        val bitacora =
            BitacoraPostInmersion(

                // MySQL genera el ID
                id = idEnEdicion ?: 0,

                preChequeoId =
                    preChequeoId,

                centro =
                    centro.ifBlank {
                        "Centro Puerto Montt"
                    },

                buzo =
                    buzoNom,

                fechaHora =
                    fecha,

                tiempoFondoMin =
                    tFondo,

                profundidadMaxM =
                    prof,

                velocidadDescensoMMin =
                    vDesc,

                velocidadAscensoMMin =
                    vAsc,

                observaciones =
                    observaciones.trim(),

                estadoRevision =
                    "PENDIENTE"
            )



        // POST SPRING BOOT


        viewModelScope.launch {

            try {
                val bitacoraFinal: BitacoraPostInmersion =

                if (idEnEdicion == null) {

                    // POST - CREAR NUEVA BITÁCORA


                    val respuesta =
                        repository.crear(
                            bitacora.toDto()
                        )

                    val guardada =
                        respuesta.toBitacora()

                    registrosBitacora.add(
                        0,
                        guardada
                    )

                    Log.d(
                        "API_BITACORA",
                        "Bitácora guardada correctamente. ID: ${respuesta.id}"
                    )

                    // Devolvemos la bitácora creada
                    guardada

                } else {

                    // PUT - ACTUALIZAR BITÁCORA


                    val respuesta =
                        repository.actualizar(
                            bitacora.id.toLong(),
                            bitacora.toDto()
                        )

                    val actualizada =
                        respuesta.toBitacora()

                    val posicion =
                        registrosBitacora.indexOfFirst {
                            it.id == actualizada.id
                        }

                    if (posicion >= 0) {

                        registrosBitacora[posicion] =
                            actualizada
                    }

                    Log.d(
                        "API_BITACORA",
                        "Bitácora actualizada correctamente. ID: ${respuesta.id}"
                    )

                    // Devolvemos la bitácora actualizada
                    actualizada
                }



                // ALERTAS


                if (bitacoraFinal.tieneAlerta) {

                    bitacoraFinal.alertasJefeCentro
                        .forEach { alerta ->

                            Log.w(
                                "API_BITACORA",
                                alerta
                            )
                        }
                }

                // LIMPIAR DESPUÉS DE POST O PUT EXITOSO
                limpiarCampos()
                errorServidor = null



            } catch (e: retrofit2.HttpException) {

                val detalle =
                    e.response()
                        ?.errorBody()
                        ?.string()

                errorServidor =
                    "Error del servidor: HTTP ${e.code()}"

                Log.e(
                    "API_BITACORA",
                    "Error POST Bitácora HTTP ${e.code()} | $detalle"
                )

            } catch (e: Exception) {

                errorServidor =
                    "No se pudo guardar la bitácora"

                Log.e(
                    "API_BITACORA",
                    "Error POST Bitácora: ${e.message}",
                    e
                )
            }
        }
    }

    private fun limpiarCampos() {

        idEnEdicion = null
        fechaEnEdicion = null
        tiempoFondoStr = ""
        profundidadStr = ""
        velDescensoStr = ""
        velAscensoStr = ""
        observaciones = ""

        error = null
        errorServidor = null
    }



    //ELIMINAR BITACORA

    fun eliminar(
        bitacora: BitacoraPostInmersion
    ) {

        val id =
            bitacora.id


        if (id <= 0) {

            Log.e(
                "API_BITACORA",
                "No se puede eliminar una bitácora sin ID válido"
            )

            return
        }


        viewModelScope.launch {

            try {

                repository.eliminar(
                    id.toLong()
                )


                registrosBitacora.removeAll {
                    it.id == id
                }

                // Si justo estábamos editando esta bitácora,
                // salir del modo edición
                if (idEnEdicion == id) {

                    nuevaBitacora()
                }


                Log.d(
                    "API_BITACORA",
                    "Bitácora eliminada correctamente. ID: $id"
                )


            } catch (e: retrofit2.HttpException) {

                if (e.code() == 404) {

                    registrosBitacora.removeAll {
                        it.id == id
                    }

                    if (idEnEdicion == id) {

                        nuevaBitacora()
                    }

                    Log.d(
                        "API_BITACORA",
                        "Bitácora ya no existía en servidor. Eliminada de Android. ID: $id"
                    )

                } else {

                    Log.e(
                        "API_BITACORA",
                        "Error DELETE Bitácora HTTP ${e.code()}"
                    )
                }


            } catch (e: Exception) {

                Log.e(
                    "API_BITACORA",
                    "Error eliminando Bitácora: ${e.message}"
                )
            }
        }
    }

    fun nuevaBitacora(
        nombreBuzo: String? = null
    ) {

        // Salir del modo edición
        idEnEdicion = null
        fechaEnEdicion = null

        // Limpiar formulario
        tiempoFondoStr = ""
        profundidadStr = ""
        velDescensoStr = ""
        velAscensoStr = ""
        observaciones = ""

        // Puedes mantener el centro seleccionado
        // centro = "Centro Puerto Montt"

        // Si hay un buzo conectado, dejar su nombre
        buzo = nombreBuzo ?: ""

        error = null
        errorServidor = null
    }

    fun cargarParaEditar(
        bitacora: BitacoraPostInmersion
    ) {

        idEnEdicion =
            bitacora.id

        fechaEnEdicion =
            bitacora.fechaHora

        centro =
            bitacora.centro

        buzo =
            bitacora.buzo

        tiempoFondoStr =
            bitacora.tiempoFondoMin.toString()

        profundidadStr =
            bitacora.profundidadMaxM.toString()

        velDescensoStr =
            bitacora.velocidadDescensoMMin.toString()

        velAscensoStr =
            bitacora.velocidadAscensoMMin.toString()

        observaciones =
            bitacora.observaciones

        error = null
    }



    //PATCH REVISAR
    fun revisar(
        bitacora: BitacoraPostInmersion,
        responsable: String
    ) {

        if (bitacora.id <= 0) {

            Log.e(
                "API_BITACORA",
                "No se puede revisar una bitácora sin ID válido"
            )

            return
        }


        viewModelScope.launch {

            try {

                val respuesta =
                    repository.revisar(
                        id = bitacora.id.toLong(),
                        responsable = responsable
                    )


                val revisada =
                    respuesta.toBitacora()


                val posicion =
                    registrosBitacora.indexOfFirst {
                        it.id == revisada.id
                    }


                if (posicion >= 0) {

                    registrosBitacora[posicion] =
                        revisada
                }


                Log.d(
                    "API_BITACORA",
                    "Bitácora revisada correctamente. ID: ${respuesta.id}"
                )


            } catch (e: retrofit2.HttpException) {

                val detalle =
                    e.response()
                        ?.errorBody()
                        ?.string()


                Log.e(
                    "API_BITACORA",
                    "Error PATCH Bitácora HTTP ${e.code()} | $detalle"
                )


            } catch (e: Exception) {

                Log.e(
                    "API_BITACORA",
                    "Error revisando Bitácora: ${e.message}"
                )
            }
        }
    }
}
