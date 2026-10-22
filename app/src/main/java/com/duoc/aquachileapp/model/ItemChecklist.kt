package com.duoc.aquachileapp.model

data class ItemChecklist(
    val nombre: String,
    val critico: Boolean = false,
    val estado: Estado = Estado.PENDIENTE,
    val observacion: String = "",
    val fotos: List<String> = emptyList()
)
