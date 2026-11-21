package com.duoc.aquachileapp.data.Remote

import com.duoc.aquachileapp.model.BitacoraPostInmersion
import retrofit2.http.*

//esto corresponde a GET http://localhost:8080/api/usuarios
interface ApiService {

    //USUARIO
    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<UsuarioDto>

    @POST("/api/usuarios")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): UsuarioDto

    //PATCH aprobar
    @PATCH("/api/usuarios/{id}/aprobar")
    suspend fun aprobarUsuario(@Path("id")id:Long): UsuarioDto

    //PUT actualizar
    @PUT("/api/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id")id:Long, @Body usuario: UsuarioDto): UsuarioDto

    //DELETE eliminar
    @DELETE("/api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id")id:Long)



    //PRE-CHEQUEOS
    @GET("api/prechequeos")
    suspend fun listarPreChequeos():
            List<PreChequeoDto>


    @GET("api/prechequeos/{id}")
    suspend fun buscarPreChequeo(
        @Path("id") id: Long
    ): PreChequeoDto


    @POST("api/prechequeos")
    suspend fun crearPreChequeo(
        @Body preChequeo: PreChequeoDto
    ): PreChequeoDto


    @PUT("api/prechequeos/{id}")
    suspend fun actualizarPreChequeo(
        @Path("id") id: Long,
        @Body preChequeo: PreChequeoDto
    ): PreChequeoDto


    @DELETE("api/prechequeos/{id}")
    suspend fun eliminarPreChequeo(
        @Path("id") id: Long
    )


    // EVIDENCIAS EQUIPO

    @GET("api/evidencias")
    suspend fun listarEvidencias():
            List<EvidenciaEquipoDto>


    @GET("api/evidencias/faena/{idFaena}")
    suspend fun listarEvidenciasPorFaena(
        @Path("idFaena") idFaena: String
    ): List<EvidenciaEquipoDto>


    @POST("api/evidencias")
    suspend fun crearEvidencia(
        @Body evidencia: EvidenciaEquipoDto
    ): EvidenciaEquipoDto

    @PUT("api/evidencias/{id}")
    suspend fun actualizarEvidencia(
        @Path("id") id: Long,
        @Body evidencia: EvidenciaEquipoDto
    ): EvidenciaEquipoDto


    @DELETE("api/evidencias/{id}")
    suspend fun eliminarEvidencia(
        @Path("id") id: Long
    )


    // BITÁCORA POST-INMERSIÓN

    // GET
    @GET("api/bitacoras")
    suspend fun listarBitacoras():
            List<BitacoraDto>


    // GET por ID
    @GET("api/bitacoras/{id}")
    suspend fun buscarBitacora(
        @Path("id") id: Long
    ): BitacoraDto


    // POST
    @POST("api/bitacoras")
    suspend fun crearBitacora(
        @Body bitacora: BitacoraDto
    ): BitacoraDto

    // PUT
    @PUT("api/bitacoras/{id}")
    suspend fun actualizarBitacora(
        @Path("id") id: Long,
        @Body bitacora: BitacoraDto
    ): BitacoraDto


    // DELETE
    @DELETE("api/bitacoras/{id}")
    suspend fun eliminarBitacora(
        @Path("id") id: Long
    )


    //PATCH revisar la bitacora
    @PATCH("api/bitacoras/{id}/revisar")
    suspend fun revisarBitacora(
        @Path("id") id: Long,
        @Query("responsable") responsable: String
    ): BitacoraDto


}