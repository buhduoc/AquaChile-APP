package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.ApiService
import com.duoc.aquachileapp.data.Remote.EvidenciaEquipoDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class EvidenciaEquipoRepositoryTest {
    private lateinit var api: ApiService
    private lateinit var repository: EvidenciaEquipoRepository

    @Before
    fun preparar() {
        api = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.api } returns api
        repository = EvidenciaEquipoRepository()
    }

    @After
    fun limpiar() {
        unmockkObject(RetrofitClient)
    }

    @Test
    fun listar_respuestaCorrecta() = runTest {
        val esperado = listOf(mockk<EvidenciaEquipoDto>())
        coEvery { api.listarEvidencias() } returns esperado
        assertSame(esperado, repository.listar())
        coVerify(exactly = 1) { api.listarEvidencias() }
        confirmVerified(api)
    }

    @Test
    fun listar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.listarEvidencias() } throws error
        try {
            repository.listar()
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.listarEvidencias() }
        confirmVerified(api)
    }

    @Test
    fun listarPorFaena_respuestaCorrecta() = runTest {
        val esperado = listOf(mockk<EvidenciaEquipoDto>())
        coEvery { api.listarEvidenciasPorFaena("faena-42") } returns esperado
        assertSame(esperado, repository.listarPorFaena("faena-42"))
        coVerify(exactly = 1) { api.listarEvidenciasPorFaena("faena-42") }
        confirmVerified(api)
    }

    @Test
    fun listarPorFaena_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.listarEvidenciasPorFaena("faena-42") } throws error
        try {
            repository.listarPorFaena("faena-42")
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.listarEvidenciasPorFaena("faena-42") }
        confirmVerified(api)
    }

    @Test
    fun crear_respuestaCorrecta() = runTest {
        val entrada = mockk<EvidenciaEquipoDto>()
        val esperado = mockk<EvidenciaEquipoDto>()
        coEvery { api.crearEvidencia(entrada) } returns esperado
        assertSame(esperado, repository.crear(entrada))
        coVerify(exactly = 1) { api.crearEvidencia(entrada) }
        confirmVerified(api)
    }

    @Test
    fun crear_propagaError() = runTest {
        val entrada = mockk<EvidenciaEquipoDto>()
        val error = IOException("Sin conexion")
        coEvery { api.crearEvidencia(entrada) } throws error
        try {
            repository.crear(entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.crearEvidencia(entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_respuestaCorrecta() = runTest {
        val entrada = mockk<EvidenciaEquipoDto>()
        val esperado = mockk<EvidenciaEquipoDto>()
        coEvery { api.actualizarEvidencia(42L, entrada) } returns esperado
        assertSame(esperado, repository.actualizar(42L, entrada))
        coVerify(exactly = 1) { api.actualizarEvidencia(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_propagaError() = runTest {
        val entrada = mockk<EvidenciaEquipoDto>()
        val error = IOException("Sin conexion")
        coEvery { api.actualizarEvidencia(42L, entrada) } throws error
        try {
            repository.actualizar(42L, entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.actualizarEvidencia(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_respuestaCorrecta() = runTest {
        coEvery { api.eliminarEvidencia(42L) } returns Unit
        repository.eliminar(42L)
        coVerify(exactly = 1) { api.eliminarEvidencia(42L) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.eliminarEvidencia(42L) } throws error
        try {
            repository.eliminar(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.eliminarEvidencia(42L) }
        confirmVerified(api)
    }
}
