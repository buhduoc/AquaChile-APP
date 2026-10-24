package com.duoc.aquachileapp.model

data class Usuario(
    val nombre: String,
    val rol: String,
    val rut: String,
    val correo: String,
    val telefono: String
) {
    val esSupervisor: Boolean
        get() = rol.equals("Supervisor", ignoreCase = true)
}
