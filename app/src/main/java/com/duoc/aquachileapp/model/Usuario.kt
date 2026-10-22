package com.duoc.aquachileapp.model

data class
Usuario(
    val nombre: String,
    val rol: String
) {
    val esSupervisor: Boolean
        get() = rol == "Supervisor"
}