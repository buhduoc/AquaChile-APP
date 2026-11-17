package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.BitacoraPostInmersion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BitacoraViewModel : ViewModel() {

    val registrosBitacora = mutableStateListOf(
        BitacoraPostInmersion(
            id = 1,
            preChequeoId = 3,
            centro = "Centro Calbuco",
            buzo = "Carlos Muñoz",
            fechaHora = "17-10-2026 09:30",
            tiempoFondoMin = 35,
            profundidadMaxM = 18.5,
            velocidadDescensoMMin = 18.0,
            velocidadAscensoMMin = 7.5,
            observaciones = "Inmersión normal dentro de parámetros"
        ),
        BitacoraPostInmersion(
            id = 2,
            preChequeoId = 2,
            centro = "Centro Hornopirén",
            buzo = "Andrés Vera",
            fechaHora = "16-10-2026 15:45",
            tiempoFondoMin = 42,
            profundidadMaxM = 22.0,
            velocidadDescensoMMin = 28.5,
            velocidadAscensoMMin = 11.2,
            observaciones = "Ascenso rápido detectado por sensor de profundidad"
        )
    )

    var centro by mutableStateOf("Centro Puerto Montt")
    var buzo by mutableStateOf("")
    var tiempoFondoStr by mutableStateOf("")
    var profundidadStr by mutableStateOf("")
    var velDescensoStr by mutableStateOf("")
    var velAscensoStr by mutableStateOf("")
    var observaciones by mutableStateOf("")

    var error by mutableStateOf<String?>(null)

    fun registrar(preChequeoId: Int? = null, nombreBuzo: String? = null): BitacoraPostInmersion? {
        val tFondo = tiempoFondoStr.toIntOrNull()
        val prof = profundidadStr.toDoubleOrNull()
        val vDesc = velDescensoStr.toDoubleOrNull()
        val vAsc = velAscensoStr.toDoubleOrNull()
        val buzoNom = if (buzo.isNotBlank()) buzo else nombreBuzo ?: "Buzo A Cargo"

        error = when {
            tFondo == null || tFondo <= 0 -> "Ingresa un tiempo de fondo válido (minutos)"
            prof == null || prof <= 0 -> "Ingresa una profundidad máxima válida (metros)"
            vDesc == null || vDesc < 0 -> "Ingresa la velocidad de descenso (m/min)"
            vAsc == null || vAsc < 0 -> "Ingresa la velocidad de ascenso (m/min)"
            else -> null
        }

        if (error != null || tFondo == null || prof == null || vDesc == null || vAsc == null) {
            return null
        }

        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        val bitacora = BitacoraPostInmersion(
            id = registrosBitacora.size + 1,
            preChequeoId = preChequeoId,
            centro = centro.ifBlank { "Centro Puerto Montt" },
            buzo = buzoNom,
            fechaHora = fecha,
            tiempoFondoMin = tFondo,
            profundidadMaxM = prof,
            velocidadDescensoMMin = vDesc,
            velocidadAscensoMMin = vAsc,
            observaciones = observaciones.trim()
        )

        registrosBitacora.add(0, bitacora)
        limpiarCampos()
        return bitacora
    }

    private fun limpiarCampos() {
        tiempoFondoStr = ""
        profundidadStr = ""
        velDescensoStr = ""
        velAscensoStr = ""
        observaciones = ""
        error = null
    }
}
