package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.AppSection

class HomeViewModel: ViewModel() {

    // Guarda la sección seleccionada actualmente
    var seccion by mutableStateOf(AppSection.PRECHEQUEO)
        private set


    // Cambia la sección seleccionada
    fun seleccionar(item: AppSection) {

        seccion = item
    }
}