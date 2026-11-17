package com.duoc.aquachileapp.model

data class Cuenta(
    val usuario: String,
    val clave: String,
    val nombre: String,
    val rol: String,
    val rut: String,
    val telefono: String,
    val correo: String,
    val aprobada: Boolean,
    val edad: Int = 45,
    val pesoKg: Double = 80.0,
    val alturaCm: Double = 172.0,
    val examenPreocupacionalVigente: Boolean = true,
    val testEsfuerzoVigente: Boolean = false,
    val puntajeEscuelaCalbuco: Int = 85
)
