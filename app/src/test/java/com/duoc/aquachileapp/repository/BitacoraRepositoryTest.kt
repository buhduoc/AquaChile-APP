package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.Remote.ApiService
import com.duoc.aquachileapp.data.Remote.BitacoraDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class BitacoraRepositoryTest {
    private lateinit var api: ApiService
    private lateinit var repository: BitacoraRepository

    @Before
    fun preparar() {
        api = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.api } returns api
        repository = BitacoraRepository()
    }

    @After
    fun limpiar() {
        unmockkObject(RetrofitClient)
    }

    @Test
    fun listar_respuestaCorrecta() = runTest {
        val esperado = listOf(mockk<BitacoraDto>())
        coEvery { api.listarBitacoras() } returns esperado
        assertSame(esperado, repository.listar())
        coVerify(exactly = 1) { api.listarBitacoras() }
        confirmVerified(api)
    }

    @Test
    fun listar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.listarBitacoras() } throws error
        try {
            repository.listar()
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.listarBitacoras() }
        confirmVerified(api)
    }

    @Test
    fun buscar_respuestaCorrecta() = runTest {
        val esperado = mockk<BitacoraDto>()
        coEvery { api.buscarBitacora(42L) } returns esperado
        assertSame(esperado, repository.buscar(42L))
        coVerify(exactly = 1) { api.buscarBitacora(42L) }
        confirmVerified(api)
    }

    @Test
    fun buscar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.buscarBitacora(42L) } throws error
        try {
            repository.buscar(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.buscarBitacora(42L) }
        confirmVerified(api)
    }

    @Test
    fun crear_respuestaCorrecta() = runTest {
        val entrada = mockk<BitacoraDto>()
        val esperado = mockk<BitacoraDto>()
        coEvery { api.crearBitacora(entrada) } returns esperado
        assertSame(esperado, repository.crear(entrada))
        coVerify(exactly = 1) { api.crearBitacora(entrada) }
        confirmVerified(api)
    }

    @Test
    fun crear_propagaError() = runTest {
        val entrada = mockk<BitacoraDto>()
        val error = IOException("Sin conexion")
        coEvery { api.crearBitacora(entrada) } throws error
        try {
            repository.crear(entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.crearBitacora(entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_respuestaCorrecta() = runTest {
        val entrada = mockk<BitacoraDto>()
        val esperado = mockk<BitacoraDto>()
        coEvery { api.actualizarBitacora(42L, entrada) } returns esperado
        assertSame(esperado, repository.actualizar(42L, entrada))
        coVerify(exactly = 1) { api.actualizarBitacora(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun actualizar_propagaError() = runTest {
        val entrada = mockk<BitacoraDto>()
        val error = IOException("Sin conexion")
        coEvery { api.actualizarBitacora(42L, entrada) } throws error
        try {
            repository.actualizar(42L, entrada)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.actualizarBitacora(42L, entrada) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_respuestaCorrecta() = runTest {
        coEvery { api.eliminarBitacora(42L) } returns Unit
        repository.eliminar(42L)
        coVerify(exactly = 1) { api.eliminarBitacora(42L) }
        confirmVerified(api)
    }

    @Test
    fun eliminar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.eliminarBitacora(42L) } throws error
        try {
            repository.eliminar(42L)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.eliminarBitacora(42L) }
        confirmVerified(api)
    }

    @Test
    fun revisar_respuestaCorrecta() = runTest {
        val esperado = mockk<BitacoraDto>()
        coEvery { api.revisarBitacora(42L, "Supervisor") } returns esperado
        assertSame(esperado, repository.revisar(42L, "Supervisor"))
        coVerify(exactly = 1) { api.revisarBitacora(42L, "Supervisor") }
        confirmVerified(api)
    }

    @Test
    fun revisar_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.revisarBitacora(42L, "Supervisor") } throws error
        try {
            repository.revisar(42L, "Supervisor")
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.revisarBitacora(42L, "Supervisor") }
        confirmVerified(api)
    }
}
