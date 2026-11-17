package com.duoc.aquachileapp.model

import com.duoc.aquachileapp.R

data class EvidenciaEquipo(
    val idEquipo: String,
    val nombreEquipo: String,
    val obligatoria: Boolean = true,
    val uriFotoActual: String? = null,
    val fechaHora: String? = null,
    val idFaena: String? = null,
    val responsable: String? = null,
    val resFotoReferencia: Int = R.drawable.aquachile_logo
)

val EVIDENCIAS_OBLIGATORIAS_ZARPE = listOf(
    EvidenciaEquipo(
        idEquipo = "maleta_aire",
        nombreEquipo = "Maleta de Suministro de Aire",
        resFotoReferencia = R.drawable.maleta_suministro
    ),
    EvidenciaEquipo(
        idEquipo = "filtros_compresor",
        nombreEquipo = "Filtros del Compresor",
        resFotoReferencia = R.drawable.filtros_compresor
    ),
    EvidenciaEquipo(
        idEquipo = "aletas_buceo",
        nombreEquipo = "Aletas de Buceo",
        resFotoReferencia = R.drawable.aletas
    ),
    EvidenciaEquipo(
        idEquipo = "dispositivo_census",
        nombreEquipo = "Dispositivo Census",
        resFotoReferencia = R.drawable.dispositivo_census
    )
)
