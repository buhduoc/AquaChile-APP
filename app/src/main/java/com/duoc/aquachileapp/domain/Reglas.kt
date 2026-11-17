package com.duoc.aquachileapp.domain

import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.NivelSalud
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.model.ResultadoValidacion
import com.duoc.aquachileapp.model.SemaforoRiesgo
import kotlin.math.roundToInt

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

    fun evaluarValidacion(
        edad: Int,
        pesoKg: Double,
        alturaCm: Double,
        examenPreocupacionalVigente: Boolean,
        testEsfuerzoVigente: Boolean,
        puntajeEscuelaCalbuco: Int,
        sistolica: Int,
        diastolica: Int,
        saturacion: Int,
        alertasSalud: List<String>,
        itemsChecklist: List<ItemChecklist>
    ): ResultadoValidacion {
        val motivosBloqueo = mutableListOf<String>()
        val advertencias = mutableListOf<String>()

        // 1. Cálculo de IMC
        val alturaM = if (alturaCm > 0) alturaCm / 100.0 else 1.70
        val rawImc = if (alturaM > 0) pesoKg / (alturaM * alturaM) else 22.0
        val imc = (rawImc * 10.0).roundToInt() / 10.0

        val imcCategoria = when {
            imc < 18.5 -> "Bajo peso"
            imc < 25.0 -> "Peso normal"
            imc < 30.0 -> "Sobrepeso"
            imc < 35.0 -> "Obesidad Grado I"
            else -> "Obesidad Severa"
        }

        if (imc >= 35.0) {
            motivosBloqueo.add("IMC crítico de $imc ($imcCategoria) excede el límite seguro para buceo")
        } else if (imc >= 25.0) {
            advertencias.add("IMC en rango de $imcCategoria ($imc kg/m²)")
        }

        // 2. Edad + Test de esfuerzo
        if (edad > 45 && !testEsfuerzoVigente) {
            motivosBloqueo.add("Buzo de $edad años (mayor de 45) sin Test de Esfuerzo vigente")
        } else if (edad > 40 && !testEsfuerzoVigente) {
            advertencias.add("Buzo de $edad años requiere renovar pronto su Test de Esfuerzo")
        }

        // 3. Examen preocupacional
        if (!examenPreocupacionalVigente) {
            motivosBloqueo.add("Examen preocupacional no vigente o no presentado")
        }

        // 4. Puntaje Escuela de Buceo de Calbuco
        if (puntajeEscuelaCalbuco < 70) {
            motivosBloqueo.add("Puntaje en Escuela de Buceo de Calbuco insuficiente ($puntajeEscuelaCalbuco/100 pts, mín. 70)")
        } else if (puntajeEscuelaCalbuco < 85) {
            advertencias.add("Puntaje regular en Escuela de Buceo Calbuco ($puntajeEscuelaCalbuco/100 pts)")
        }

        // 5. Parámetros de Salud & Encuesta Preventiva
        if (saturacion < 90) {
            motivosBloqueo.add("Saturación de oxígeno crítica ($saturacion%, mín. 90%)")
        } else if (saturacion < 95) {
            advertencias.add("Saturación de O₂ en nivel de atención ($saturacion%)")
        }

        if (sistolica >= 160 || diastolica >= 100) {
            motivosBloqueo.add("Presión arterial en crisis hipertensiva ($sistolica/$diastolica mmHg)")
        } else if (sistolica >= 140 || diastolica >= 90) {
            motivosBloqueo.add("Presión arterial en nivel hipertensivo de riesgo ($sistolica/$diastolica mmHg)")
        } else if (sistolica >= 130 || diastolica >= 85) {
            advertencias.add("Presión arterial algo elevada ($sistolica/$diastolica mmHg)")
        }

        if (alertasSalud.isNotEmpty()) {
            advertencias.add("${alertasSalud.size} alerta(s) médica(s) registrada(s) en encuesta preventiva")
        }

        // 6. Checklist e Ítems Críticos
        val itemsCriticosFallados = itemsChecklist.filter { it.critico && it.estado == Estado.NO_CUMPLE }
        if (itemsCriticosFallados.isNotEmpty()) {
            itemsCriticosFallados.forEach { item ->
                motivosBloqueo.add("Ítem crítico en 'No cumple': ${item.nombre}")
            }
        }

        val itemsObservados = itemsChecklist.filter { it.estado == Estado.OBSERVADO }
        if (itemsObservados.isNotEmpty()) {
            advertencias.add("${itemsObservados.size} ítem(s) de seguridad en estado 'Observado'")
        }

        // Determinar Semáforo Final
        val estaBloqueado = motivosBloqueo.isNotEmpty()
        val semaforo = when {
            estaBloqueado -> SemaforoRiesgo.ROJO
            advertencias.isNotEmpty() -> SemaforoRiesgo.AMARILLO
            else -> SemaforoRiesgo.VERDE
        }

        return ResultadoValidacion(
            semaforo = semaforo,
            imc = imc,
            imcCategoria = imcCategoria,
            estaBloqueado = estaBloqueado,
            motivosBloqueo = motivosBloqueo,
            advertencias = advertencias
        )
    }
}
