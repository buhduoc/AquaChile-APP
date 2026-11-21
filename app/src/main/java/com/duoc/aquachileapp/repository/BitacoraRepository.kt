package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.BitacoraDto
import com.duoc.aquachileapp.data.Remote.RetrofitClient

class BitacoraRepository {

    private val api =
        RetrofitClient.api


    // GET
    suspend fun listar():
            List<BitacoraDto> {

        return api.listarBitacoras()
    }


    // GET por ID
    suspend fun buscar(
        id: Long
    ): BitacoraDto {

        return api.buscarBitacora(id)
    }


    // POST
    suspend fun crear(
        bitacora: BitacoraDto
    ): BitacoraDto {

        return api.crearBitacora(
            bitacora
        )
    }

    // PUT
    suspend fun actualizar(
        id: Long,
        bitacora: BitacoraDto
    ): BitacoraDto {

        return api.actualizarBitacora(
            id,
            bitacora
        )
    }


    // DELETE
    suspend fun eliminar(
        id: Long
    ) {

        api.eliminarBitacora(id)
    }

    //PATCH
    suspend fun revisar(
        id: Long,
        responsable: String
    ): BitacoraDto {

        return api.revisarBitacora(
            id = id,
            responsable = responsable
        )
    }

}