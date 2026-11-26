package com.duoc.aquachileapp.viewmodel
import android.util.Log
import com.duoc.aquachileapp.data.Remote.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import java.io.IOException
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
@OptIn(ExperimentalCoroutinesApi::class)
class BitacoraViewModelTest {
 private lateinit var api: ApiService
 @Before fun setup() {
  Dispatchers.setMain(StandardTestDispatcher())
  mockkStatic(Log::class)
  every { Log.d(any(),any()) } returns 0
  every { Log.e(any(),any()) } returns 0
  every { Log.e(any(),any(),any()) } returns 0
  every { Log.w(any<String>(),any<String>()) } returns 0
  api=mockk()
  mockkObject(RetrofitClient)
  every { RetrofitClient.api } returns api
  coEvery { api.listarBitacoras() } returns listOf(dto)
 }
 @After fun cleanup() {
  unmockkObject(RetrofitClient); unmockkStatic(Log::class); Dispatchers.resetMain()
 }
 private fun http(code:Int)=HttpException(Response.error<Unit>(code,"error".toResponseBody()))
 private val dto=BitacoraDto(id=7,centro="Centro",buzo="Buzo",fechaHora="23-11-2026 10:00",tiempoFondoMin=20,profundidadMaxM=10.0,velocidadDescensoMMin=25.0,velocidadAscensoMMin=10.0)
 @Test fun validaciones()=runTest {
  val vm=BitacoraViewModel(); advanceUntilIdle()
  vm.registrar(); assertEquals("Ingresa un tiempo de fondo válido (minutos)",vm.error)
  vm.tiempoFondoStr="0"; vm.registrar(); assertNotNull(vm.error)
  vm.tiempoFondoStr="20"; vm.registrar(); assertEquals("Ingresa una profundidad máxima válida (metros)",vm.error)
  vm.profundidadStr="0"; vm.registrar(); assertNotNull(vm.error)
  vm.profundidadStr="10"; vm.registrar(); assertEquals("Ingresa la velocidad de descenso (m/min)",vm.error)
  vm.velDescensoStr="-1"; vm.registrar(); assertNotNull(vm.error)
  vm.velDescensoStr="5"; vm.registrar(); assertEquals("Ingresa la velocidad de ascenso (m/min)",vm.error)
  vm.velAscensoStr="-1"; vm.registrar(); assertNotNull(vm.error)
  coVerify(exactly=0){api.crearBitacora(any())}
 }
 @Test fun cargaYError()=runTest {
  val vm=BitacoraViewModel(); advanceUntilIdle(); assertEquals(7,vm.registrosBitacora.single().id)
  coEvery { api.listarBitacoras() } throws IOException(); vm.cargarDesdeServidor(); advanceUntilIdle(); assertEquals("No se pudieron cargar las bitácoras",vm.errorServidor); assertFalse(vm.cargandoServidor)
 }
 @Test fun crearEditarRevisarEliminar()=runTest {
  val vm=BitacoraViewModel(); advanceUntilIdle(); vm.nuevaBitacora("Otro"); vm.centro=""; vm.tiempoFondoStr="20"; vm.profundidadStr="10"; vm.velDescensoStr="25"; vm.velAscensoStr="10"
  coEvery { api.crearBitacora(any()) } returns dto.copy(id=8); vm.registrar(2); advanceUntilIdle(); assertEquals(8,vm.registrosBitacora.first().id); assertEquals("",vm.tiempoFondoStr)
  coVerify { api.crearBitacora(match { it.buzo=="Otro" && it.preChequeoId==2L && it.centro=="Centro Puerto Montt" }) }
  vm.cargarParaEditar(vm.registrosBitacora.first()); assertEquals(8,vm.idEnEdicion); coEvery { api.actualizarBitacora(8,any()) } returns dto.copy(id=8,observaciones="Editada"); vm.registrar(); advanceUntilIdle(); assertEquals("Editada",vm.registrosBitacora.first().observaciones)
  coEvery { api.revisarBitacora(8,"Jefe") } returns dto.copy(id=8,estadoRevision="REVISADA"); vm.revisar(vm.registrosBitacora.first(),"Jefe"); advanceUntilIdle(); assertEquals("REVISADA",vm.registrosBitacora.first().estadoRevision)
  vm.cargarParaEditar(vm.registrosBitacora.first()); coEvery { api.eliminarBitacora(8) } returns Unit; vm.eliminar(vm.registrosBitacora.first()); advanceUntilIdle(); assertNull(vm.idEnEdicion); assertEquals(7,vm.registrosBitacora.single().id)
 }
 @Test fun erroresServidor()=runTest {
  val vm=BitacoraViewModel(); advanceUntilIdle(); val item=vm.registrosBitacora.single(); vm.cargarParaEditar(item)
  coEvery { api.actualizarBitacora(7,any()) } throws http(500); vm.registrar(); advanceUntilIdle(); assertEquals("Error del servidor: HTTP 500",vm.errorServidor)
  coEvery { api.actualizarBitacora(7,any()) } throws IOException(); vm.registrar(); advanceUntilIdle(); assertEquals("No se pudo guardar la bitácora",vm.errorServidor)
  vm.eliminar(item.copy(id=0)); vm.revisar(item.copy(id=0),"Jefe")
  coEvery { api.revisarBitacora(7,"Jefe") } throws http(500); vm.revisar(item,"Jefe"); advanceUntilIdle(); coEvery { api.revisarBitacora(7,"Jefe") } throws IOException(); vm.revisar(item,"Jefe"); advanceUntilIdle()
  coEvery { api.eliminarBitacora(7) } throws http(500); vm.eliminar(item); advanceUntilIdle(); assertEquals(1,vm.registrosBitacora.size)
  coEvery { api.eliminarBitacora(7) } throws IOException(); vm.eliminar(item); advanceUntilIdle(); assertEquals(1,vm.registrosBitacora.size)
  coEvery { api.eliminarBitacora(7) } throws http(404); vm.eliminar(item); advanceUntilIdle(); assertTrue(vm.registrosBitacora.isEmpty()); assertNull(vm.idEnEdicion)
 }
}
