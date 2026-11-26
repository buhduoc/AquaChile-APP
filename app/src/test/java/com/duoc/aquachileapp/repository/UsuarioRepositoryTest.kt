package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.ApiService
import com.duoc.aquachileapp.data.Remote.UsuarioDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class UsuarioRepositoryTest {
    private lateinit var api: ApiService
    private lateinit var repository: UsuarioRepository

    @Before
    fun preparar() {
        api = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.api } returns api
        repository = UsuarioRepository()
    }

    @After
    fun limpiar() {
        unmockkObject(RetrofitClient)
    }

    @Test
    fun listarUsuarios_respuestaCorrecta() = runTest {
        val esperado = listOf(mockk<UsuarioDto>())
        coEvery { api.listarUsuarios() } returns esperado
        assertSame(esperado, repository.listarUsuarios())
        coVerify(exactly = 1) { api.listarUsuarios() }
        confirmVerified(api)
    }

    @Test
    fun listarUsuarios_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.listarUsuarios() } throws error
        try {
            repository.listarUsuarios()
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.listarUsuarios() }
        confirmVerified(api)
    }

    @Test
    fun registrarUsuario_respuestaCorrecta() = runTest {
        val entrada = mockk<UsuarioDto>()
        val esperado = mockk<UsuarioDto>()
        coEvery { api.registrarUsuario(entrada) } returns esperado
        assertSame(esperado, repository.registrarUsuario(entrada))
        coVerify(exactly = 1) { api.registrarUsuario(entrada) }
        confirmVerified(api)
    }

    @Test
    fun registrarUsuario_propagaError() = runTest {
        val entrada = mockk<UsuarioDto>()
        val error = IOException("Sin conexion")
        coEvery { api.registrarUsuario(entrada) } throws error
        try {
            repository.registrarUsuario(entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.registrarUsuario(entrada) }
        confirmVerified(api)
    }

    @Test
    fun aprobarUsuario_respuestaCorrecta() = runTest {
        val esperado = mockk<UsuarioDto>()
        coEvery { api.aprobarUsuario(42L) } returns esperado
        assertSame(esperado, repository.aprobarUsuario(42L))
        coVerify(exactly = 1) { api.aprobarUsuario(42L) }
        confirmVerified(api)
    }

    @Test
    fun aprobarUsuario_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.aprobarUsuario(42L) } throws error
        try {
            repository.aprobarUsuario(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.aprobarUsuario(42L) }
        confirmVerified(api)
    }

    @Test
    fun actualizarUsuario_respuestaCorrecta() = runTest {
        val entrada = mockk<UsuarioDto>()
        val esperado = mockk<UsuarioDto>()
        coEvery { api.actualizarUsuario(42L, entrada) } returns esperado
        assertSame(esperado, repository.actualizarUsuario(42L, entrada))
        coVerify(exactly = 1) { api.actualizarUsuario(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizarUsuario_propagaError() = runTest {
        val entrada = mockk<UsuarioDto>()
        val error = IOException("Sin conexion")
        coEvery { api.actualizarUsuario(42L, entrada) } throws error
        try {
            repository.actualizarUsuario(42L, entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.actualizarUsuario(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun eliminarUsuario_respuestaCorrecta() = runTest {
        coEvery { api.eliminarUsuario(42L) } returns Unit
        repository.eliminarUsuario(42L)
        coVerify(exactly = 1) { api.eliminarUsuario(42L) }
        confirmVerified(api)
    }

    @Test
    fun eliminarUsuario_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.eliminarUsuario(42L) } throws error
        try {
            repository.eliminarUsuario(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.eliminarUsuario(42L) }
        confirmVerified(api)
    }
}
