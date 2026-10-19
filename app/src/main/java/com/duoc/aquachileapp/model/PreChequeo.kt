package com.duoc.aquachileapp.model

enum class Estado(val texto: String) {
    CUMPLE("Cumple"),
    NO_CUMPLE("No cumple"),
    OBSERVADO("Observado"),
    NA("N/A")
}

    val resultado: Resultado
        get() = when {
            items.any { it.critico && it.estado == Estado.NO_CUMPLE } -> Resultado.REQUIERE_REVISION
            items.any { it.estado == Estado.NO_CUMPLE || it.estado == Estado.OBSERVADO } ||
                alertasEncuesta.isNotEmpty() -> Resultado.OBSERVADO
            else -> Resultado.CUMPLE
        }

data class PreChequeo(
    val id: Int,
    val centro: String,
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val items: List<ItemChecklist>,
    val observaciones: String = "",
    val fotos: List<String> = emptyList(),
    val sistolica: Int = 0,
    val diastolica: Int = 0,
    val saturacion: Int = 0,
    val alertasEncuesta: List<String> = emptyList()
) {
    val resultado: Resultado
        get() = when {
            items.any { it.critico && !it.aprobado } -> Resultado.REQUIERE_REVISION
            items.any { !it.aprobado } || alertasEncuesta.isNotEmpty() -> Resultado.OBSERVADO
            else -> Resultado.CUMPLE
        }
}