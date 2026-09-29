package com.duoc.aquachileapp.model

data class PreChequeo(
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val items: List<ItemChecklist>
)
