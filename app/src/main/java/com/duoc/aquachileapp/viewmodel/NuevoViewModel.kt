package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.domain.Reglas
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.NivelSalud
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Usuario
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NuevoViewModel : ViewModel() {

    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")
    var supervisor by mutableStateOf("")

    val items = CHECKLIST.toMutableStateList()

    var observaciones by mutableStateOf("")

    val fotos = mutableStateListOf<String>()

    var sistolica by mutableStateOf("")
    var diastolica by mutableStateOf("")
    var saturacion by mutableStateOf("")

    val alertas = mutableStateListOf<String>()

    var error by mutableStateOf<String?>(null)

    val salud: NivelSalud?
        get() {
            val sis = sistolica.toIntOrNull() ?: return null
            val dia = diastolica.toIntOrNull() ?: return null
            val sat = saturacion.toIntOrNull() ?: return null
            return Reglas.nivelSalud(sis, dia, sat, alertas.size)
        }

    fun prepararDatos(usuario: Usuario?) {
        if (centro.isBlank()) centro = "Centro Puerto Montt"
        if (usuario == null) return

        if (usuario.esSupervisor) {
            if (supervisor.isBlank()) supervisor = usuario.nombre
        } else {
            if (buzo.isBlank()) buzo = usuario.nombre
        }
    }

    fun cambiarEstado(indice: Int, estado: Estado) {
        items[indice] = items[indice].copy(estado = estado)
        error = null
    }

    fun cambiarObservacion(indice: Int, observacion: String) {
        items[indice] = items[indice].copy(observacion = observacion)
        error = null
    }

    fun agregarFotoItem(indice: Int, uri: String) {
        if (indice in items.indices) {
            val lista = items[indice].fotos.toMutableList()
            if (uri !in lista) {
                lista.add(uri)
                items[indice] = items[indice].copy(fotos = lista)
            }
        }
        if (uri !in fotos) {
            fotos.add(uri)
        }
    }

    fun eliminarFotoItem(indiceItem: Int, indiceFoto: Int) {
        if (indiceItem in items.indices) {
            val lista = items[indiceItem].fotos.toMutableList()
            if (indiceFoto in lista.indices) {
                val fotoEliminada = lista.removeAt(indiceFoto)
                items[indiceItem] = items[indiceItem].copy(fotos = lista)
                if (items.none { fotoEliminada in it.fotos }) {
                    fotos.remove(fotoEliminada)
                }
            }
        }
    }

    fun marcarAlerta(pregunta: String) {
        if (pregunta in alertas) {
            alertas.remove(pregunta)
        } else {
            alertas.add(pregunta)
        }
    }

    fun crear(): PreChequeo? {
        val sis = sistolica.toIntOrNull()
        val dia = diastolica.toIntOrNull()
        val sat = saturacion.toIntOrNull()
        val hayObservacion = items.any {
            it.estado == Estado.NO_CUMPLE || it.estado == Estado.OBSERVADO
        }

        error = when {
            centro.isBlank() || buzo.isBlank() || supervisor.isBlank() ->
                "Completa el centro, el buzo y el supervisor"
            items.any { it.estado == Estado.PENDIENTE } ->
                "Evalúa todos los ítems del checklist"
            hayObservacion && observaciones.isBlank() ->
                "Escribe una observación que justifique"
            sis == null || dia == null || sat == null ->
                "Completa presión y saturación"
            sat !in 50..100 || sis !in 70..250 || dia !in 40..150 ->
                "Valores de salud fuera de rango"
            else -> null
        }

        if (error != null || sis == null || dia == null || sat == null) {
            return null
        }

        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        return PreChequeo(
            id = 0,
            centro = centro.trim(),
            fecha = fecha,
            buzo = buzo.trim(),
            supervisor = supervisor.trim(),
            items = items.toList(),
            observaciones = observaciones.trim(),
            fotos = fotos.toList(),
            sistolica = sis,
            diastolica = dia,
            saturacion = sat,
            alertas = alertas.toList()
        )
    }

    fun limpiar() {
        centro = "Centro Puerto Montt"
        buzo = ""
        supervisor = ""
        items.clear()
        items.addAll(CHECKLIST)
        observaciones = ""
        fotos.clear()
        sistolica = ""
        diastolica = ""
        saturacion = ""
        alertas.clear()
        error = null
    }
}
