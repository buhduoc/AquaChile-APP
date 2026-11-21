package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.UsuarioDto
import com.duoc.aquachileapp.model.Usuario


class UsuarioRepository {

    private val api =
        RetrofitClient.api


    //GET
    suspend fun listarUsuarios():
            List<UsuarioDto> {

        return api.listarUsuarios()
    }

    //POST
    suspend fun registrarUsuario(usuario: UsuarioDto): UsuarioDto{
        return api.registrarUsuario(usuario)
    }

    //PUTCH
    suspend fun aprobarUsuario(id:Long): UsuarioDto{
        return api.aprobarUsuario(id)
    }

    //PUT
    suspend fun actualizarUsuario(id:Long, usuario: UsuarioDto): UsuarioDto{
        return api.actualizarUsuario(id,usuario)
    }

    //DELETE
    suspend fun eliminarUsuario(id:Long){
        return api.eliminarUsuario(id)
    }

}