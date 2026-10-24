package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.Cuenta

class CuentasViewModel : ViewModel() {

    val cuentas = mutableStateListOf(
        Cuenta("supervisor@aquachile.cl", "supervisor123", "Supervisor Demo", "Supervisor", "14.500.600-6","+56 9 92434555","supervisor@aquachile.cl",true),
        Cuenta("12345678-9", "buzo123", "Buzo Demo", "Buzo", "17.550.431-8","+56 9 61638242","buzoDemo@aquachile.cl",true),
        Cuenta("98765432-1", "buzo123", "Juan Pérez (Solicitud Pendiente)", "Buzo", "19.990.567-2","+56 9 30737458","juanPerez@aquachile.cl",false)
    )

    val pendientes: List<Cuenta>
        get() = cuentas.filter { !it.aprobada }

    fun registrar(usuario: String, clave: String, nombre: String, rut: String,telefono: String,correo: String): String? {
        val id = limpiar(usuario)

        if (nombre.isBlank() || id.isBlank() || clave.isBlank()||rut.isBlank()||telefono.isBlank()||correo.isBlank()) {
            return "Completa todos los campos"
        }

        if (clave.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        if (cuentas.any { it.usuario.equals(id, ignoreCase = true) }) {
            return "Ese usuario ya existe"
        }

        cuentas.add(Cuenta(id, clave, nombre.trim(), "Buzo", rut = rut.trim(),telefono=telefono.trim(),
            correo=correo.trim(), false))
        return null
    }

    fun aprobar(cuenta: Cuenta) {
        val posicion = cuentas.indexOf(cuenta)
        if (posicion >= 0) {
            cuentas[posicion] = cuenta.copy(aprobada = true)
        }
    }

    fun eliminar(cuenta: Cuenta) {
        cuentas.remove(cuenta)
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
