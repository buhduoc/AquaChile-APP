package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.LoginRequest
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

    var cargando by mutableStateOf(false)
        private set

    fun iniciarSesion(onSuccess: (Usuario) -> Unit) {
        if (cargando) return
        val u = usuario.trim()
        val p = contrasena
        if (u.isBlank() || p.isBlank()) {
            error = "Completa tu correo/RUT y contraseña"
            return
        }
        cargando = true
        viewModelScope.launch {
            try {
                val perfil = RetrofitClient.api.login(LoginRequest(u, p))
                error = null
                contrasena = ""
                val u = Usuario(nombre = perfil.nombre, rol = perfil.rol, rut = perfil.rut, correo = perfil.correo, telefono = perfil.telefono)
                com.duoc.aquachileapp.data.Remote.SesionSegura.guardar(requireNotNull(perfil.token), u)
                onSuccess(u)
            } catch (e: kotlinx.coroutines.CancellationException) { throw e
            } catch (e: retrofit2.HttpException) {
                error = when (e.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    403 -> "Tu cuenta está pendiente de aprobación por un supervisor"
                    else -> "Error del servidor"
                }
            } catch (e: Exception) {
                error = "No se pudo conectar con el servidor"
            } finally { cargando = false }
        }
    }
}
