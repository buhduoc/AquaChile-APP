package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.PreChequeoDto
import com.duoc.aquachileapp.data.Remote.RetrofitClient

class PreChequeoRepository {

    private val api =
        RetrofitClient.api


    suspend fun listar():
            List<PreChequeoDto> {

        return api.listarPreChequeos()
    }


    suspend fun crear(
        preChequeo: PreChequeoDto
    ): PreChequeoDto {

        return api.crearPreChequeo(
            preChequeo
        )
    }


    suspend fun actualizar(
        id: Long,
        preChequeo: PreChequeoDto
    ): PreChequeoDto {

        return api.actualizarPreChequeo(
            id,
            preChequeo
        )
    }


    suspend fun eliminar(
        id: Long
    ) {

        api.eliminarPreChequeo(id)
    }

}