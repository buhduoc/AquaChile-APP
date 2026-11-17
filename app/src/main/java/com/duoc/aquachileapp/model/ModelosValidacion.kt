package com.duoc.aquachileapp.model

import androidx.compose.ui.graphics.Color

enum class SemaforoRiesgo(
    val titulo: String,
    val color: Color,
    val requiereBloqueo: Boolean
) {
    VERDE("VERDE - APTO PARA INMERSIÓN", Color(0xFF10B981), false),
    AMARILLO("AMARILLO - APTO CON PRECAUCIÓN", Color(0xFFD97706), false),
    ROJO("ROJO - BLOQUEADO / NO APTO", Color(0xFFDC2626), true)
}

data class ResultadoValidacion(
    val semaforo: SemaforoRiesgo,
    val imc: Double,
    val imcCategoria: String,
    val estaBloqueado: Boolean,
    val motivosBloqueo: List<String>,
    val advertencias: List<String>
)
