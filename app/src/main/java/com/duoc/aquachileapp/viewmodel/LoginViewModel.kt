package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.Usuario

class LoginViewModel : ViewModel() {

    var usuario by mutableStateOf("")
        private set

    var contrasena by mutableStateOf("")
        private set

    var contrasenaVisible by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onUsuarioChange(valor: String) {
        usuario = valor
        error = null
    }

    fun onContrasenaChange(valor: String) {
        contrasena = valor
        error = null
    }

    fun alternarVisibilidad() {
        contrasenaVisible = !contrasenaVisible
    }

    fun iniciarSesion(cuentas: CuentasViewModel): Usuario? {

        if (usuario.isBlank() || contrasena.isBlank()) {
            error = "Completa tu RUT o correo y tu contraseña"
            return null
        }

        val cuenta = cuentas.buscar(usuario, contrasena)

        if (cuenta == null) {
            error = "Credenciales incorrectas"
            return null
        }

        if (!cuenta.aprobada) {
            error = "Tu cuenta aún no ha sido aprobada por un supervisor"
            return null
        }

        error = null
        return Usuario(nombre = cuenta.nombre, rol = cuenta.rol)
    }
}