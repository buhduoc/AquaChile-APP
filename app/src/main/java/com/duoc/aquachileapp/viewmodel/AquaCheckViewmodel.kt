package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.ItemChecklist

class AquaCheckViewmodel: ViewModel() {
    val checklist = mutableStateListOf(
        ItemChecklist("Compresor", false),
        ItemChecklist("Oxigeno", false),
        ItemChecklist("Señalizacion", false)
    )

    fun cambiarEstado(index: Int){
        checklist[index] = checklist[index].copy(
            aprobado = !checklist[index].aprobado
        )
    }

}