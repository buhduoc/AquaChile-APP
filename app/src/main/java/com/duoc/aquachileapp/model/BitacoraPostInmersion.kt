package com.duoc.aquachileapp.model

data class  BitacoraPostInmersion(
    val id: Int,
    val preChequeoId: Int?,
    val centro: String,
    val buzo: String,
    val fechaHora: String,
    val tiempoFondoMin: Int,
    val profundidadMaxM: Double,
    val velocidadDescensoMMin: Double,
    val velocidadAscensoMMin: Double,
    val observaciones: String = "",

    // Datos de revisión
    val estadoRevision: String = "PENDIENTE",
    val responsableRevision: String? = null,
    val fechaRevision: String? = null

) {

    val excesoDescenso: Boolean
        get() =
            velocidadDescensoMMin > 24.0


    val excesoAscenso: Boolean
        get() =
            velocidadAscensoMMin > 9.0


    val tieneAlerta: Boolean
        get() =
            excesoDescenso || excesoAscenso


    val alertasJefeCentro: List<String>
        get() {

            val lista =
                mutableListOf<String>()


            if (excesoDescenso) {

                lista.add(
                    "ALERTA JEFE DE CENTRO: " +
                            "Velocidad de descenso excede el límite " +
                            "($velocidadDescensoMMin m/min, máx. 24 m/min)"
                )
            }


            if (excesoAscenso) {

                lista.add(
                    "ALERTA CRÍTICA JEFE DE CENTRO: " +
                            "Velocidad de ascenso excede el límite seguro " +
                            "($velocidadAscensoMMin m/min, máx. 9 m/min)"
                )
            }


            return lista
        }
}