package com.duoc.aquachileapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aquachileapp.data.Remote.toCuenta
import com.duoc.aquachileapp.data.Remote.toDto
import com.duoc.aquachileapp.model.Cuenta
import com.duoc.aquachileapp.repository.UsuarioRepository
import kotlinx.coroutines.launch

class CuentasViewModel : ViewModel() {

    // REPOSITORY - CONEXIÓN CON SPRING BOOT
    private val repository = UsuarioRepository()



    // LISTA DE CUENTAS


    /*
     * Antes las cuentas estaban escritas manualmente aquí.
     *
     * Ahora se cargarán desde Spring Boot / MySQL.
     */
    val cuentas = mutableStateListOf<Cuenta>()



    // ESTADOS DE LA API


    var cargando by mutableStateOf(false)
        private set

    var errorApi by mutableStateOf<String?>(null)
        private set



    // AL CREAR EL VIEWMODEL


    init {
        cargarUsuarios()
    }



    // GET - CARGAR USUARIOS DESDE SPRING BOOT


    fun cargarUsuarios() {

        viewModelScope.launch {

            cargando = true

            try {

                val usuariosApi =
                    repository.listarUsuarios()


                // Eliminamos la lista local anterior
                cuentas.clear()


                // Convertimos UsuarioDto -> Cuenta
                cuentas.addAll(
                    usuariosApi.map {
                        it.toCuenta()
                    }
                )


                errorApi = null


                Log.d(
                    "API_USUARIOS",
                    "Usuarios cargados: ${cuentas.size}"
                )


            } catch (e: Exception) {

                errorApi =
                    "No se pudo conectar con el servidor"


                Log.e(
                    "API_USUARIOS",
                    "Error al cargar usuarios: ${e.message}"
                )


            } finally {

                cargando = false
            }
        }
    }



    // CUENTAS PENDIENTES


    val pendientes: List<Cuenta>
        get() = cuentas.filter { !it.aprobada }


    // REGISTRAR


    fun registrar(
        usuario: String,
        clave: String,
        nombre: String,
        rut: String,
        telefono: String,
        correo: String
    ): String? {

        val id = limpiar(usuario)


        // Validar campos vacíos
        if (
            nombre.isBlank() ||
            id.isBlank() ||
            clave.isBlank() ||
            rut.isBlank() ||
            telefono.isBlank() ||
            correo.isBlank()
        ) {

            return "Completa todos los campos"
        }


        // Validar contraseña
        if (clave.length < 6) {

            return "La contraseña debe tener al menos 6 caracteres"
        }


        // Revisar si ya está cargado localmente
        if (
            cuentas.any {
                it.usuario.equals(
                    id,
                    ignoreCase = true
                )
            }
        ) {

            return "Ese usuario ya existe"
        }


        // Creamos la cuenta
        val nuevaCuenta = Cuenta(
            usuario = id,
            clave = clave,
            nombre = nombre.trim(),
            rol = "Buzo",
            rut = rut.trim(),
            telefono = telefono.trim(),
            correo = correo.trim(),
            aprobada = false
        )


        // Enviar a Spring Boot
        viewModelScope.launch {

            try {

                val respuesta =
                    repository.registrarUsuario(
                        nuevaCuenta.toDto()
                    )


                // Solo la agregamos a Android
                // si Spring Boot la guardó correctamente
                cuentas.add(
                    respuesta.toCuenta()
                )


                errorApi = null


                Log.d(
                    "API_USUARIOS",
                    "Usuario registrado correctamente. ID: ${respuesta.id}"
                )


            } catch (e: Exception) {

                errorApi =
                    "No se pudo registrar el usuario"


                Log.e(
                    "API_USUARIOS",
                    "Error al registrar usuario: ${e.message}"
                )
            }
        }

        return null;
    }


    // APROBAR CUENTA


    fun aprobar(cuenta: Cuenta) {

        val id = cuenta.id

        if (id == null) {

            Log.e(
                "API_USUARIOS",
                "No se puede aprobar: usuario sin ID"
            )

            return
        }


        viewModelScope.launch {

            try {

                val respuesta =
                    repository.aprobarUsuario(id)


                val posicion =
                    cuentas.indexOfFirst {
                        it.id == id
                    }


                if (posicion >= 0) {

                    cuentas[posicion] =
                        respuesta.toCuenta()
                }


                Log.d(
                    "API_USUARIOS",
                    "Usuario aprobado. ID: $id"
                )


            } catch (e: Exception) {

                errorApi =
                    "No se pudo aprobar el usuario"


                Log.e(
                    "API_USUARIOS",
                    "Error al aprobar: ${e.message}"
                )
            }
        }
    }


    // ELIMINAR

    fun eliminar(cuenta: Cuenta) {

        val id = cuenta.id

        if (id == null) {

            Log.e(
                "API_USUARIOS",
                "No se puede eliminar: usuario sin ID"
            )

            return
        }


        viewModelScope.launch {

            try {

                repository.eliminarUsuario(id)


                cuentas.removeAll {
                    it.id == id
                }


                Log.d(
                    "API_USUARIOS",
                    "Usuario eliminado. ID: $id"
                )


            } catch (e: Exception) {

                errorApi =
                    "No se pudo eliminar el usuario"


                Log.e(
                    "API_USUARIOS",
                    "Error al eliminar: ${e.message}"
                )
            }
        }
    }

    //ACTUALIZAR DATOS


    // ACTUALIZAR DATOS BIOMÉTRICOS
    //queda en forma local hasta cambiar los nuevos datos edad hasta puntajeEscuelaCalbuco
    //o agregear que pertenecen a Usuario o una nueva Clase

    fun actualizarBiometricos(
        usuarioId: String,
        edad: Int,
        pesoKg: Double,
        alturaCm: Double,
        examenVigente: Boolean,
        testVigente: Boolean,
        puntajeCalbuco: Int
    ) {

        val id =
            limpiar(usuarioId)


        val pos =
            cuentas.indexOfFirst {

                it.usuario.equals(
                    id,
                    ignoreCase = true
                ) ||
                        it.correo.equals(
                            id,
                            ignoreCase = true
                        )
            }


        if (pos >= 0) {

            cuentas[pos] =
                cuentas[pos].copy(

                    edad = edad,

                    pesoKg = pesoKg,

                    alturaCm = alturaCm,

                    examenPreocupacionalVigente =
                        examenVigente,

                    testEsfuerzoVigente =
                        testVigente,

                    puntajeEscuelaCalbuco =
                        puntajeCalbuco
                )
        }
    }


    // BUSCAR USUARIO


    /*
     * Ahora permite iniciar sesión usando:
     *
     * - usuario / RUT
     * - correo
     */

    fun buscar(
        usuario: String,
        clave: String
    ): Cuenta? {

        val texto =
            usuario.trim()


        val id =
            limpiar(texto)


        return cuentas.firstOrNull { cuenta ->


            // Comparar usuario / RUT
            val coincideUsuario =

                limpiar(cuenta.usuario)
                    .equals(
                        id,
                        ignoreCase = true
                    )


            // Comparar correo
            val coincideCorreo =

                cuenta.correo
                    .trim()
                    .equals(
                        texto,
                        ignoreCase = true
                    )


            // Debe coincidir usuario/correo
            // y además la contraseña
            (coincideUsuario || coincideCorreo) &&
                    cuenta.clave == clave
        }
    }



    // LIMPIAR USUARIO / RUT

    private fun limpiar(
        usuario: String
    ): String {

        val texto =
            usuario.trim()


        return if (
            texto.contains("@")
        ) {

            // Si es correo lo deja igual
            texto

        } else {

            // Si es RUT quita los puntos
            texto.replace(".", "")
        }
    }
}
