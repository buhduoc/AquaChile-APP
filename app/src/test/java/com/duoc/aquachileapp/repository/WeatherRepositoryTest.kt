package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.WeatherRetrofitClient
import com.duoc.aquachileapp.data.Remote.WeatherApiService
import com.duoc.aquachileapp.data.Remote.WeatherResponseDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class WeatherRepositoryTest {
    private lateinit var api: WeatherApiService
    private lateinit var repository: WeatherRepository

    @Before
    fun preparar() {
        api = mockk()
        mockkObject(WeatherRetrofitClient)
        every { WeatherRetrofitClient.api } returns api
        repository = WeatherRepository()
    }

    @After
    fun limpiar() {
        unmockkObject(WeatherRetrofitClient)
    }

    @Test
    fun obtenerClima_respuestaCorrecta() = runTest {
        val esperado = mockk<WeatherResponseDto>()
        coEvery { api.obtenerClima(-41.47, -72.94) } returns esperado
        assertSame(esperado, repository.obtenerClima(-41.47, -72.94))
        coVerify(exactly = 1) { api.obtenerClima(-41.47, -72.94) }
        confirmVerified(api)
    }

    @Test
    fun obtenerClima_propagaError() = runTest {
        val error = IOException("Sin conexion")
        coEvery { api.obtenerClima(-41.47, -72.94) } throws error
        try {
            repository.obtenerClima(-41.47, -72.94)
            fail("Se esperaba IOException")
        } catch (e: IOException) {
            assertSame(error, e)
        }
        coVerify(exactly = 1) { api.obtenerClima(-41.47, -72.94) }
        confirmVerified(api)
    }
}
