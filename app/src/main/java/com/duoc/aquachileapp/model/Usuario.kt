package com.duoc.aquachileapp.model

data class Usuario(
    val nombre: String,
    val rol: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val edad: Int = 45,
    val pesoKg: Double = 80.0,
    val alturaCm: Double = 172.0,
    val examenPreocupacionalVigente: Boolean = true,
    val testEsfuerzoVigente: Boolean = false,
    val puntajeEscuelaCalbuco: Int = 85
) {
    val esSupervisor: Boolean
        get() = rol.equals("Supervisor", ignoreCase = true)
}
