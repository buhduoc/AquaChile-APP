package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.EvidenciaEquipoDto
import com.duoc.aquachileapp.data.Remote.RetrofitClient

class EvidenciaEquipoRepository {

    private val api =
        RetrofitClient.api


    // GET todas
    suspend fun listar():
            List<EvidenciaEquipoDto> {

        return api.listarEvidencias()
    }


    // GET por faena
    suspend fun listarPorFaena(
        idFaena: String
    ): List<EvidenciaEquipoDto> {

        return api.listarEvidenciasPorFaena(
            idFaena
        )
    }


    // POST
    suspend fun crear(
        evidencia: EvidenciaEquipoDto
    ): EvidenciaEquipoDto {

        return api.crearEvidencia(
            evidencia
        )
    }

    // PUT
    suspend fun actualizar(
        id: Long,
        evidencia: EvidenciaEquipoDto
    ): EvidenciaEquipoDto {

        return api.actualizarEvidencia(
            id,
            evidencia
        )
    }


    // DELETE
    suspend fun eliminar(
        id: Long
    ) {

        api.eliminarEvidencia(id)
    }
}