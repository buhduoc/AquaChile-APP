package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.ApiService
import com.duoc.aquachileapp.data.Remote.PreChequeoDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class PreChequeoRepositoryTest {
    private lateinit var api: ApiService
    private lateinit var repository: PreChequeoRepository

    @Before
    fun preparar() {
        api = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.api } returns api
        repository = PreChequeoRepository()
    }

    @After
    fun limpiar() {
        unmockkObject(RetrofitClient)
    }

    @Test
    fun listar_respuestaCorrecta() = runTest {
        val esperado = listOf(mockk<PreChequeoDto>())
        coEvery { api.listarPreChequeos() } returns esperado
        assertSame(esperado, repository.listar())
        coVerify(exactly = 1) { api.listarPreChequeos() }
        confirmVerified(api)
    }

    @Test
    fun listar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.listarPreChequeos() } throws error
        try {
            repository.listar()
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.listarPreChequeos() }
        confirmVerified(api)
    }

    @Test
    fun crear_respuestaCorrecta() = runTest {
        val entrada = mockk<PreChequeoDto>()
        val esperado = mockk<PreChequeoDto>()
        coEvery { api.crearPreChequeo(entrada) } returns esperado
        assertSame(esperado, repository.crear(entrada))
        coVerify(exactly = 1) { api.crearPreChequeo(entrada) }
        confirmVerified(api)
    }

    @Test
    fun crear_propagaError() = runTest {
        val entrada = mockk<PreChequeoDto>()
        val error = IOException("Sin conexion")
        coEvery { api.crearPreChequeo(entrada) } throws error
        try {
            repository.crear(entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.crearPreChequeo(entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_respuestaCorrecta() = runTest {
        val entrada = mockk<PreChequeoDto>()
        val esperado = mockk<PreChequeoDto>()
        coEvery { api.actualizarPreChequeo(42L, entrada) } returns esperado
        assertSame(esperado, repository.actualizar(42L, entrada))
        coVerify(exactly = 1) { api.actualizarPreChequeo(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_propagaError() = runTest {
        val entrada = mockk<PreChequeoDto>()
        val error = IOException("Sin conexion")
        coEvery { api.actualizarPreChequeo(42L, entrada) } throws error
        try {
            repository.actualizar(42L, entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.actualizarPreChequeo(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_respuestaCorrecta() = runTest {
        coEvery { api.eliminarPreChequeo(42L) } returns Unit
        repository.eliminar(42L)
        coVerify(exactly = 1) { api.eliminarPreChequeo(42L) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.eliminarPreChequeo(42L) } throws error
        try {
            repository.eliminar(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.eliminarPreChequeo(42L) }
        confirmVerified(api)
    }
}
