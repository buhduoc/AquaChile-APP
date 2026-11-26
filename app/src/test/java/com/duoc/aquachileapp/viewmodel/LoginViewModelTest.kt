package com.duoc.aquachileapp.viewmodel

import com.duoc.aquachileapp.data.Remote.*
import com.duoc.aquachileapp.model.Usuario
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var api: ApiService
    private val perfil = UsuarioDto(7L,"buzo",null,"Prueba","Buzo","123","456","test@example.com",true,"token-prueba")
    @Before fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        api = mockk()
        mockkObject(RetrofitClient, SesionSegura)
        every { RetrofitClient.api } returns api
        every { SesionSegura.guardar(any(), any()) } just Runs
    }
    @After fun cleanup() {
        unmockkObject(RetrofitClient, SesionSegura)
        Dispatchers.resetMain()
    }
    @Test fun camposVaciosYVisibilidad() {
        val vm = LoginViewModel()
        assertEquals("",vm.usuario); assertEquals("",vm.contrasena)
        assertFalse(vm.contrasenaVisible)
        vm.iniciarSesion { fail("No debe autenticar") }
        assertEquals("Completa tu correo/RUT y contraseña",vm.error)
        vm.onUsuarioChange("buzo"); assertNull(vm.error)
        vm.iniciarSesion { fail("No debe autenticar") }
        assertNotNull(vm.error)
        vm.onContrasenaChange("clave"); assertNull(vm.error)
        vm.alternarVisibilidad(); assertTrue(vm.contrasenaVisible)
        vm.alternarVisibilidad(); assertFalse(vm.contrasenaVisible)
        coVerify(exactly=0) { api.login(any()) }
    }
    @Test fun inicioCorrecto() = runTest {
        coEvery { api.login(LoginRequest("buzo"," clave ")) } returns perfil
        val vm=LoginViewModel(); var resultado: Usuario?=null
        vm.onUsuarioChange(" buzo "); vm.onContrasenaChange(" clave ")
        vm.iniciarSesion { resultado=it }; assertTrue(vm.cargando)
        vm.iniciarSesion { fail("Peticion duplicada") }
        advanceUntilIdle()
        assertEquals(Usuario(perfil.nombre,perfil.rol,perfil.rut,perfil.correo,perfil.telefono),resultado)
        assertNull(vm.error); assertEquals("",vm.contrasena); assertFalse(vm.cargando)
        coVerify(exactly=1) { api.login(LoginRequest("buzo"," clave ")) }
        verify(exactly=1) { SesionSegura.guardar("token-prueba",any()) }
    }
    @Test fun erroresHttp() = runTest {
        for ((codigo,mensaje) in listOf(401 to "Usuario o contraseña incorrectos",403 to "Tu cuenta está pendiente de aprobación por un supervisor",500 to "Error del servidor")) {
            coEvery { api.login(any()) } throws HttpException(Response.error<UsuarioDto>(codigo,"".toResponseBody()))
            val vm=LoginViewModel(); vm.onUsuarioChange("buzo"); vm.onContrasenaChange("clave")
            vm.iniciarSesion { fail("No debe autenticar") }; advanceUntilIdle()
            assertEquals(mensaje,vm.error); assertFalse(vm.cargando)
        }
        verify(exactly=0) { SesionSegura.guardar(any(),any()) }
    }
    @Test fun sinConexionYTokenAusente() = runTest {
        val vm=LoginViewModel(); vm.onUsuarioChange("buzo"); vm.onContrasenaChange("clave")
        coEvery { api.login(any()) } throws java.io.IOException()
        vm.iniciarSesion { fail("No debe autenticar") }; advanceUntilIdle()
        assertEquals("No se pudo conectar con el servidor",vm.error); assertFalse(vm.cargando)
        coEvery { api.login(any()) } returns perfil.copy(token=null)
        vm.iniciarSesion { fail("No debe autenticar") }; advanceUntilIdle()
        assertNotNull(vm.error); assertFalse(vm.cargando)
        verify(exactly=0) { SesionSegura.guardar(any(),any()) }
    }
}
