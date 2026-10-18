package com.duoc.aquachileapp.model

enum class Resultado(val texto: String) {
    CUMPLE("Cumple"),
    OBSERVADO("Observado"),
    REQUIERE_REVISION("Requiere revisión")
}

data class PreChequeo(
    val id: Int,
    val centro: String,
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val items: List<ItemChecklist>
) {
    val resultado: Resultado
        get() = when {
            items.any { it.critico && !it.aprobado } -> Resultado.REQUIERE_REVISION
            items.any { !it.aprobado } -> Resultado.OBSERVADO
            else -> Resultado.CUMPLE
        }
}