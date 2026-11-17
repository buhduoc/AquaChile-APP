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
        val u = usuario.trim()
        val p = contrasena.trim()

        if (u.isBlank() || p.isBlank()) {
            error = "Completa tu correo/RUT y contraseña"
            return null
        }

        val existeUsuario = cuentas.cuentas.any {
            it.usuario.equals(u, ignoreCase = true) || it.correo.equals(u, ignoreCase = true)
        }

        if (!existeUsuario) {
            error = "El correo o RUT ingresado no está registrado"
            return null
        }

        val cuenta = cuentas.buscar(u, p)

        if (cuenta == null) {
            error = "La contraseña ingresada es incorrecta"
            return null
        }

        if (!cuenta.aprobada) {
            error = "Tu cuenta está pendiente de aprobación por un supervisor"
            return null
        }

        error = null
        return Usuario(
            nombre = cuenta.nombre,
            rol = cuenta.rol,
            rut = cuenta.rut,
            telefono = cuenta.telefono,
            correo = cuenta.correo
        )
    }
}
