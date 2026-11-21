package com.duoc.aquachileapp.data.Remote

import com.duoc.aquachileapp.R
import com.duoc.aquachileapp.model.EvidenciaEquipo


data class EvidenciaEquipoDto(

    val id: Long? = null,
    val idEquipo: String,
    val nombreEquipo: String,
    val obligatoria: Boolean,
    val uriFotoActual: String?,
    val fotoReferencia: String?,
    val fechaHora: String?,
    val idFaena: String?,
    val responsable: String?
)

fun EvidenciaEquipo.toDto(
    idFaenaServidor: String,
    responsableDefecto: String
): EvidenciaEquipoDto {

    val referencia =
        when (idEquipo) {

            "maleta_aire" ->
                "maleta_suministro"

            "filtros_compresor" ->
                "filtros_compresor"

            "aletas_buceo" ->
                "aletas"

            "dispositivo_census" ->
                "dispositivo_census"

            else ->
                null
        }


    return EvidenciaEquipoDto(

        id = id,

        idEquipo = idEquipo,

        nombreEquipo = nombreEquipo,

        obligatoria = obligatoria,

        uriFotoActual = uriFotoActual,

        fotoReferencia = referencia,

        fechaHora = fechaHora,

        // Usamos el ID real del PreChequeo
        // generado por MySQL
        idFaena = idFaenaServidor,

        responsable =
            responsable
                ?: responsableDefecto
    )
}



//Para recibir datos desde Spring boot

fun EvidenciaEquipoDto.toEvidenciaEquipo():
        EvidenciaEquipo {

    val referenciaAndroid =
        when (idEquipo) {

            "maleta_aire" ->
                R.drawable.maleta_suministro_1

            "filtros_compresor" ->
                R.drawable.filtros_compresor

            "aletas_buceo" ->
                R.drawable.aletas

            "dispositivo_census" ->
                R.drawable.dispositivo_census

            else ->
                R.drawable.aquachile_logo
        }


    return EvidenciaEquipo(

        idEquipo = idEquipo,

        nombreEquipo = nombreEquipo,

        obligatoria = obligatoria,

        uriFotoActual = uriFotoActual,

        fechaHora = fechaHora,

        idFaena = idFaena,

        responsable = responsable,

        resFotoReferencia =
            referenciaAndroid,

        id = id
    )
}