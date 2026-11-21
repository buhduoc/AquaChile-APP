package com.duoc.aquachileapp.data.Remote

import com.duoc.aquachileapp.model.BitacoraPostInmersion

data class BitacoraDto(

    val id: Long? = null,
    val preChequeoId: Long? = null,
    val centro: String? = null,
    val buzo: String? = null,
    val fechaHora: String? = null,
    val tiempoFondoMin: Int? = null,
    val profundidadMaxM: Double? = null,
    val velocidadDescensoMMin: Double? = null,
    val velocidadAscensoMMin: Double? = null,
    val observaciones: String? = null,
    val estadoRevision: String? = null,
    val responsableRevision: String? = null,
    val fechaRevision: String? = null
)

fun BitacoraPostInmersion.toDto():
        BitacoraDto {

    return BitacoraDto(

        id =
            if (id == 0) null
            else id.toLong(),

        preChequeoId =
            preChequeoId?.toLong(),

        centro = centro,

        buzo = buzo,

        fechaHora = fechaHora,

        tiempoFondoMin =
            tiempoFondoMin,

        profundidadMaxM =
            profundidadMaxM,

        velocidadDescensoMMin =
            velocidadDescensoMMin,

        velocidadAscensoMMin =
            velocidadAscensoMMin,

        observaciones =
            observaciones,

        estadoRevision =
            estadoRevision,

        responsableRevision =
            responsableRevision,

        fechaRevision =
            fechaRevision
    )
}

//datos de Spring boot a Android
fun BitacoraDto.toBitacora():
        BitacoraPostInmersion {

    return BitacoraPostInmersion(

        id =
            id?.toInt() ?: 0,

        preChequeoId =
            preChequeoId?.toInt(),

        centro =
            centro ?: "Sin centro",

        buzo =
            buzo ?: "Sin buzo",

        fechaHora =
            fechaHora ?: "",

        tiempoFondoMin =
            tiempoFondoMin ?: 0,

        profundidadMaxM =
            profundidadMaxM ?: 0.0,

        velocidadDescensoMMin =
            velocidadDescensoMMin ?: 0.0,

        velocidadAscensoMMin =
            velocidadAscensoMMin ?: 0.0,

        observaciones =
            observaciones ?: "",

        estadoRevision =
            estadoRevision ?: "PENDIENTE",

        responsableRevision =
            responsableRevision,

        fechaRevision =
            fechaRevision
    )
}