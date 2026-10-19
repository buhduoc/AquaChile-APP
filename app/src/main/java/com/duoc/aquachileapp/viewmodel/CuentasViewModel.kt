package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.Cuenta

class CuentasViewModel : ViewModel() {

    val cuentas = mutableStateListOf(
        Cuenta("supervisor@aquachile.cl", "supervisor123", "Supervisor Demo", "Supervisor", true),
        Cuenta("12345678-9", "buzo123", "Buzo Demo", "Buzo", true)
    )

    val pendientes: List<Cuenta>
        get() = cuentas.filter { !it.aprobada }

    fun registrar(usuario: String, clave: String, nombre: String): String? {
        val id = limpiar(usuario)

        if (nombre.isBlank() || id.isBlank() || clave.isBlank()) {
            return "Completa todos los campos"
        }

        if (clave.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        if (cuentas.any { it.usuario.equals(id, ignoreCase = true) }) {
            return "Ese usuario ya existe"
        }

        cuentas.add(Cuenta(id, clave, nombre.trim(), "Buzo", false))
        return null
    }

    fun aprobar(cuenta: Cuenta) {
        val posicion = cuentas.indexOf(cuenta)
        if (posicion >= 0) {
            cuentas[posicion] = cuenta.copy(aprobada = true)
        }
    }

    fun buscar(usuario: String, clave: String): Cuenta? {
        val id = limpiar(usuario)
        return cuentas.firstOrNull {
            it.usuario.equals(id, ignoreCase = true) && it.clave == clave
        }
    }

private fun limpiar(usuario: String): String {
    val texto = usuario.trim()
    return if (texto.contains("@")) texto else texto.replace(".", "")
}
}