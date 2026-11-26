package com.duoc.aquachileapp.data.Remote

import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.data.local.PreChequeoEntity
import com.duoc.aquachileapp.data.local.toDomain


data class ItemChecklistDto(
    val id: Long? = null,
    val nombre: String,
    val critico: Boolean,
    val estado: Estado,
    val observacion: String,
    val foto: String? = null
)


data class PreChequeoDto(
    val id: Long? = null,
    val centro: String,
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val items: List<ItemChecklistDto>,
    val fotos: List<String>,
    val sistolica: Int,
    val diastolica: Int,
    val saturacion: Int,
    val observaciones: String,
    val alertas: List<String>,
    val edad: Int,
    val pesoKg: Double,
    val alturaCm: Double,
    val examenPreocupacionalVigente: Boolean,
    val testEsfuerzoVigente: Boolean,
    val puntajeEscuelaCalbuco: Int,
    val uuid: String? = null
)

fun PreChequeoDto.toPreChequeo(): PreChequeo {

    return PreChequeo(
        id = id?.toInt() ?: 0,
        centro = centro,
        fecha = fecha,
        buzo = buzo,
        supervisor = supervisor,

        items = items.map { item ->
            ItemChecklist(
                nombre = item.nombre,
                critico = item.critico,
                estado = item.estado,
                observacion = item.observacion,
                fotos = if (item.foto.isNullOrBlank()) {
                    emptyList()
                } else {
                    listOf(item.foto)
                }
            )
        },

        observaciones = observaciones,
        fotos = fotos,

        sistolica = sistolica,
        diastolica = diastolica,
        saturacion = saturacion,

        alertas = alertas,

        edad = edad,
        pesoKg = pesoKg,
        alturaCm = alturaCm,

        examenPreocupacionalVigente =
            examenPreocupacionalVigente,

        testEsfuerzoVigente =
            testEsfuerzoVigente,

        puntajeEscuelaCalbuco =
            puntajeEscuelaCalbuco
    )
}

fun PreChequeo.toDto(): PreChequeoDto {

    return PreChequeoDto(

        id = null, // Nunca enviar el ID local como ID del servidor

        centro = centro,
        fecha = fecha,
        buzo = buzo,
        supervisor = supervisor,

        items = items.map { item ->

            ItemChecklistDto(
                nombre = item.nombre,
                critico = item.critico,
                estado = item.estado,
                observacion = item.observacion,

                // Spring Boot actualmente guarda
                // una foto por ItemChecklist
                foto = item.fotos.firstOrNull()
            )
        },

        fotos = fotos,

        sistolica = sistolica,
        diastolica = diastolica,
        saturacion = saturacion,

        observaciones = observaciones,
        alertas = alertas,

        edad = edad,
        pesoKg = pesoKg,
        alturaCm = alturaCm,

        examenPreocupacionalVigente =
            examenPreocupacionalVigente,

        testEsfuerzoVigente =
            testEsfuerzoVigente,

        puntajeEscuelaCalbuco =
            puntajeEscuelaCalbuco
    )
}


// Conversión para el envío offline: mantiene el UUID estable de Room.
fun PreChequeoEntity.toSyncDto(): PreChequeoDto =
    toDomain().toDto().copy(id = null, uuid = uuid)
