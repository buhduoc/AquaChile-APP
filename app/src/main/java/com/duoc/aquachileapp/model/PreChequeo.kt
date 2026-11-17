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
    val alertas: List<String>,
    val edad: Int = 45,
    val pesoKg: Double = 80.0,
    val alturaCm: Double = 172.0,
    val examenPreocupacionalVigente: Boolean = true,
    val testEsfuerzoVigente: Boolean = false,
    val puntajeEscuelaCalbuco: Int = 85,
    val evidenciasEquipos: List<EvidenciaEquipo> = EVIDENCIAS_OBLIGATORIAS_ZARPE
) {
    val salud: NivelSalud
        get() = Reglas.nivelSalud(sistolica, diastolica, saturacion, alertas.size)

    val resultado: Resultado
        get() = Reglas.resultado(items, salud)

    val validacion: ResultadoValidacion
        get() = Reglas.evaluarValidacion(
            edad = edad,
            pesoKg = pesoKg,
            alturaCm = alturaCm,
            examenPreocupacionalVigente = examenPreocupacionalVigente,
            testEsfuerzoVigente = testEsfuerzoVigente,
            puntajeEscuelaCalbuco = puntajeEscuelaCalbuco,
            sistolica = sistolica,
            diastolica = diastolica,
            saturacion = saturacion,
            alertasSalud = alertas,
            itemsChecklist = items
        )
}
