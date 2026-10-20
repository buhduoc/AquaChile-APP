package com.duoc.aquachileapp.domain

import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.NivelSalud
import com.duoc.aquachileapp.model.Resultado

object Reglas {

    fun nivelSalud(sistolica: Int, diastolica: Int, saturacion: Int, alertas: Int): NivelSalud {
        return when {
            saturacion < 92 || sistolica >= 160 || diastolica >= 100 -> NivelSalud.CRITICO
            saturacion < 95 || sistolica >= 140 || diastolica >= 90 || alertas > 0 -> NivelSalud.ATENCION
            else -> NivelSalud.NORMAL
        }
    }

    fun resultado(items: List<ItemChecklist>, salud: NivelSalud): Resultado {
        val criticoFalla = items.any { it.critico && it.estado == Estado.NO_CUMPLE }
        val hayObservacion = items.any {
            it.estado == Estado.NO_CUMPLE || it.estado == Estado.OBSERVADO
        }

        return when {
            criticoFalla || salud == NivelSalud.CRITICO -> Resultado.REQUIERE_REVISION
            hayObservacion || salud == NivelSalud.ATENCION -> Resultado.OBSERVADO
            else -> Resultado.CUMPLE
        }
    }
}