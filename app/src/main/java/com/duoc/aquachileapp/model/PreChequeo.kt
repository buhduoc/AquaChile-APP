package com.duoc.aquachileapp.model

import com.duoc.aquachileapp.domain.Reglas

data class PreChequeo(
    val id: Int,
    val centro: String,
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val items: List<ItemChecklist>,
    val observaciones: String,
    val fotos: List<String>,
    val sistolica: Int,
    val diastolica: Int,
    val saturacion: Int,
    val alertas: List<String>
) {
    val salud: NivelSalud
        get() = Reglas.nivelSalud(sistolica, diastolica, saturacion, alertas.size)

    val resultado: Resultado
        get() = Reglas.resultado(items, salud)
}