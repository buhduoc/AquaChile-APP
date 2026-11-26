package com.duoc.aquachileapp.viewmodel
import android.util.Log
import com.duoc.aquachileapp.data.Remote.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
 private lateinit var api: WeatherApiService
 @Before fun setup() { Dispatchers.setMain(StandardTestDispatcher()); api = mockk(); mockkObject(WeatherRetrofitClient); every { WeatherRetrofitClient.api } returns api; mockkStatic(Log::class); every { Log.d(any(),any()) } returns 0; every { Log.e(any(),any()) } returns 0 }
 @After
 fun cleanup() {
  unmockkObject(WeatherRetrofitClient)
  unmockkStatic(Log::class)
  Dispatchers.resetMain()
 }
 @Test fun centrosYError() = runTest {
  val dto = WeatherResponseDto(CurrentWeatherDto(12.0,0),null)
  val vm = WeatherViewModel()
  assertNull(vm.clima)
  for ((centro, coords) in listOf("calbuco" to (-41.7680 to -73.1273), "Hornopirén" to (-41.9694 to -72.4730), "Osorno" to (-40.5739 to -73.1335), "Otro" to (-41.4693 to -72.9424))) {
   coEvery { api.obtenerClima(coords.first,coords.second) } returns dto
   vm.cargarClima(centro); advanceUntilIdle(); assertEquals(dto,vm.clima); assertNull(vm.error); assertFalse(vm.cargando)
   coVerify { api.obtenerClima(coords.first,coords.second) }
  }
  coEvery { api.obtenerClima(any(),any()) } throws java.io.IOException()
  vm.cargarClima("Otro"); advanceUntilIdle(); assertEquals("No se pudo obtener el clima",vm.error); assertFalse(vm.cargando)
 }
 @Test fun descripciones() {
  val vm = WeatherViewModel()
  for ((codes, expected) in listOf(listOf(0) to "Despejado",listOf(1) to "Mayormente Despejado",listOf(2) to "Parcialmente Nublado",listOf(3) to "Nublado",listOf(45,48) to "Niebla",listOf(51,53,55) to "Llovizna",listOf(61,63,65) to "Lluvia",listOf(71,73,75) to "Nieve",listOf(80,81,82) to "Chubascos",listOf(95,96,99) to "Tormenta",listOf(null,999) to "Condición Variable")) for (code in codes) assertEquals(expected,vm.descripcion(code))
 }
}
