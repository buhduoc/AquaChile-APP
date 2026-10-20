package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist

class AquaCheckViewmodel : ViewModel() {

    val checklist = mutableStateListOf(
        ItemChecklist("Compresor", critico = true),
        ItemChecklist("Umbilical", critico = true),
        ItemChecklist("Oxigeno", critico = true),
        ItemChecklist("Buzo de emergencia", critico = true),
        ItemChecklist("Comunicaciones"),
        ItemChecklist("Señalizacion")
    )

    var observaciones by mutableStateOf("")

        val adjuntos = mutableStateListOf<String>()

    fun agregarAdjunto(uri: String) {
        if (uri !in adjuntos) adjuntos.add(uri)
    }

    fun quitarAdjunto(index: Int) {
        adjuntos.removeAt(index)
    }

    val completo: Boolean
        get() = checklist.all { it.estado != null }

    val requiereObservacion: Boolean
        get() = checklist.any { it.estado == Estado.NO_CUMPLE || it.estado == Estado.OBSERVADO }

    val puedeGuardar: Boolean
        get() = completo && (!requiereObservacion || observaciones.isNotBlank())

    fun cambiarEstado(index: Int, estado: Estado) {
        checklist[index] = checklist[index].copy(estado = estado)
    }
}