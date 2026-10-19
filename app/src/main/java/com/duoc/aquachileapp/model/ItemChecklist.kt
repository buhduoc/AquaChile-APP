package com.duoc.aquachileapp.model

data class ItemChecklist(
    val nombre: String,
    val aprobado: Estado? = null
    val critico: Boolean = false
)