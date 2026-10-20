package com.duoc.aquachileapp.model

data class ItemChecklist(
    val nombre: String,
    val critico: Boolean,
    val estado: Estado = Estado.PENDIENTE
)