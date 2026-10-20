package com.duoc.aquachileapp.model

enum class Estado(val texto: String) {
    PENDIENTE("Pendiente"),
    CUMPLE("Cumple"),
    NO_CUMPLE("No cumple"),
    NO_APLICA("No aplica"),
    OBSERVADO("Observado")
}

enum class Resultado(val texto: String) {
    CUMPLE("Cumple"),
    OBSERVADO("Observado"),
    REQUIERE_REVISION("Requiere revisión")
}

enum class NivelSalud(val texto: String) {
    NORMAL("Normal"),
    ATENCION("Atención"),
    CRITICO("Crítico")
}