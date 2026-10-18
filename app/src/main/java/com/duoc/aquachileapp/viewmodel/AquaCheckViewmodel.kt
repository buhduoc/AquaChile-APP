package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.ItemChecklist

class AquaCheckViewmodel: ViewModel() {
    val checklist = mutableStateListOf(
        ItemChecklist("Compresor", false, critico = true),
        ItemChecklist("Oxigeno", false, critico = true),
        ItemChecklist("Señalizacion", false)
    )

    fun cambiarEstado(index: Int){
        checklist[index] = checklist[index].copy(
            aprobado = !checklist[index].aprobado
        )
    }

}