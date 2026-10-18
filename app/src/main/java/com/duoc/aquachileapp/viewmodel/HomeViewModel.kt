package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.AppSection
import com.duoc.aquachileapp.model.Usuario

class HomeViewModel: ViewModel() {

    var seccion by mutableStateOf(AppSection.PRECHEQUEO)
        private set

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    fun seleccionar(item: AppSection) {
        seccion = item
    }

    fun iniciarSesion(nuevo: Usuario) {
        usuario = nuevo
    }

    fun cerrarSesion() {
        usuario = null
    }
}