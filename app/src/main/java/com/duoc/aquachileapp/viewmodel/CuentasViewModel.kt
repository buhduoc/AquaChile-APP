package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.Cuenta

class CuentasViewModel : ViewModel() {

    val cuentas = mutableStateListOf(
        Cuenta("supervisor@aquachile.cl", "supervisor123", "Supervisor Demo", "Supervisor", "14.500.600-6", "+56 9 92434555", "supervisor@aquachile.cl", true, edad = 42, pesoKg = 75.0, alturaCm = 175.0, examenPreocupacionalVigente = true, testEsfuerzoVigente = true, puntajeEscuelaCalbuco = 92),
        Cuenta("12345678-9", "buzo123", "Buzo Demo", "Buzo", "17.550.431-8", "+56 9 61638242", "buzoDemo@aquachile.cl", true, edad = 48, pesoKg = 82.0, alturaCm = 172.0, examenPreocupacionalVigente = true, testEsfuerzoVigente = false, puntajeEscuelaCalbuco = 88),
        Cuenta("98765432-1", "buzo123", "Juan Pérez (Solicitud Pendiente)", "Buzo", "19.990.567-2", "+56 9 30737458", "juanPerez@aquachile.cl", false, edad = 35, pesoKg = 78.0, alturaCm = 170.0, examenPreocupacionalVigente = true, testEsfuerzoVigente = true, puntajeEscuelaCalbuco = 80)
    )

    val pendientes: List<Cuenta>
        get() = cuentas.filter { !it.aprobada }

    fun registrar(usuario: String, clave: String, nombre: String, rut: String, telefono: String, correo: String): String? {
        val id = limpiar(usuario)

        if (nombre.isBlank() || id.isBlank() || clave.isBlank() || rut.isBlank() || telefono.isBlank() || correo.isBlank()) {
            return "Completa todos los campos"
        }

        if (clave.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        if (cuentas.any { it.usuario.equals(id, ignoreCase = true) }) {
            return "Ese usuario ya existe"
        }

        cuentas.add(
            Cuenta(
                id, clave, nombre.trim(), "Buzo", rut = rut.trim(), telefono = telefono.trim(),
                correo = correo.trim(), aprobada = false
            )
        )
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

    fun actualizarBiometricos(
        usuarioId: String,
        edad: Int,
        pesoKg: Double,
        alturaCm: Double,
        examenVigente: Boolean,
        testVigente: Boolean,
        puntajeCalbuco: Int
    ) {
        val id = limpiar(usuarioId)
        val pos = cuentas.indexOfFirst { it.usuario.equals(id, ignoreCase = true) || it.correo.equals(id, ignoreCase = true) }
        if (pos >= 0) {
            cuentas[pos] = cuentas[pos].copy(
                edad = edad,
                pesoKg = pesoKg,
                alturaCm = alturaCm,
                examenPreocupacionalVigente = examenVigente,
                testEsfuerzoVigente = testVigente,
                puntajeEscuelaCalbuco = puntajeCalbuco
            )
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
