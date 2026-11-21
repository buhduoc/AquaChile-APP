package com.duoc.aquachileapp.data.Remote

import com.duoc.aquachileapp.model.Cuenta

data class UsuarioDto(

    val id: Long? = null,
    val usuario: String,
    val clave: String,
    val nombre: String,
    val rol: String,
    val rut: String,
    val telefono: String,
    val correo: String,
    val aprobada: Boolean
)


fun UsuarioDto.toCuenta(): Cuenta {

    return Cuenta(
        id =id,
        usuario = usuario,
        clave = clave,
        nombre = nombre,
        rol = rol,
        rut = rut,
        telefono = telefono,
        correo = correo,
        aprobada = aprobada
    )
}

fun Cuenta.toDto(): UsuarioDto {

    return UsuarioDto(
        id = id,
        usuario = usuario,
        clave = clave,
        nombre = nombre,
        rol = rol,
        rut = rut,
        telefono = telefono,
        correo = correo,
        aprobada = aprobada
    )
}