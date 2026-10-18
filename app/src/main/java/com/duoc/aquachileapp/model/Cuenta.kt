package com.duoc.aquachileapp.model

data class Cuenta(
    val usuario: String,
    val clave: String,
    val nombre: String,
    val rol: String,
    val aprobada: Boolean
)