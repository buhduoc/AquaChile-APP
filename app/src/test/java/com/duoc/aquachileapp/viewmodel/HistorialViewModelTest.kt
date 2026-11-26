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
import android.app.Application
import com.duoc.aquachileapp.data.local.*
import com.duoc.aquachileapp.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
@OptIn(ExperimentalCoroutinesApi::class)
class HistorialViewModelTest {
 private val app=mockk<Application>()
 private lateinit var dao:PreChequeoDao
 private lateinit var flow:MutableStateFlow<List<PreChequeoEntity>>
 private val dto=PreChequeoDto(7,"Centro","Fecha","Buzo","Supervisor",emptyList(),emptyList(),120,80,98,"",emptyList(),35,72.0,175.0,true,true,90)
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
  dao=mockk(); flow=MutableStateFlow(listOf(dto.toPreChequeo().toEntity()))
  every { dao.obtenerTodos() } returns flow
  coEvery { dao.insertar(any()) } returns 7L
  mockkObject(AppDatabase.Companion,SyncManager)
  val db=mockk<AppDatabase>(); every { AppDatabase.obtenerInstancia(app) } returns db; every { db.preChequeoDao() } returns dao
  every { SyncManager.programar(app) } just Runs
 }
 @After fun cleanup() {
  unmockkObject(AppDatabase.Companion,SyncManager)
  unmockkObject(RetrofitClient); unmockkStatic(Log::class); Dispatchers.resetMain()
 }
 private fun http(code:Int)=HttpException(Response.error<Unit>(code,"error".toResponseBody()))
 @Test fun roomYGuardado()=runTest {
  val vm=HistorialViewModel(app); advanceUntilIdle(); assertEquals(7,vm.buscar(7)?.id); assertNull(vm.buscar(99)); assertEquals(flow.value.single().estadoSincronizacion,vm.estadoPorId(7))
  vm.guardar(emptyList(),"Supervisor"); advanceUntilIdle(); coVerify { dao.insertar(match { it.id==0 && it.estadoSincronizacion=="PENDIENTE" && it.idServidor==null && it.buzo=="Sin buzo" }) }; verify(exactly=2){SyncManager.programar(app)}
  coEvery { dao.insertar(any()) } throws IOException(); vm.agregar(dto.toPreChequeo()); advanceUntilIdle(); assertEquals("No se pudo guardar el prechequeo localmente",vm.errorServidor)
  flow.value=emptyList(); advanceUntilIdle(); assertTrue(vm.registros.isEmpty()); assertNull(vm.estadoPorId(7))
 }
 @Test fun cargaActualizacionYEliminacion()=runTest {
  val vm=HistorialViewModel(app); advanceUntilIdle(); val item=dto.toPreChequeo()
  coEvery { api.listarPreChequeos() } returns listOf(dto); vm.cargarDesdeServidor(); advanceUntilIdle(); assertNull(vm.errorServidor); coVerify { dao.insertar(match { it.id==7 }) }
  coEvery { api.listarPreChequeos() } throws IOException(); vm.cargarDesdeServidor(); advanceUntilIdle(); assertEquals("No se pudo actualizar desde el servidor",vm.errorServidor); assertFalse(vm.cargandoServidor)
  vm.actualizar(item.copy(id=0)); advanceUntilIdle(); coVerify(exactly=0){api.actualizarPreChequeo(any(),any())}
  coEvery { api.actualizarPreChequeo(7,any()) } returns dto.copy(observaciones="Editada"); vm.actualizar(item); advanceUntilIdle(); coVerify { dao.insertar(match { it.observaciones=="Editada" }) }
  coEvery { api.actualizarPreChequeo(7,any()) } throws IOException(); vm.actualizar(item); advanceUntilIdle()
  vm.eliminar(0); advanceUntilIdle(); coVerify(exactly=0){api.eliminarPreChequeo(any())}
  coEvery { dao.eliminarPorId(7) } returns Unit; coEvery { api.eliminarPreChequeo(7) } returns Unit; vm.eliminar(item); advanceUntilIdle(); coVerify(exactly=1){dao.eliminarPorId(7)}
  coEvery { api.eliminarPreChequeo(7) } throws http(404); vm.eliminar(7); advanceUntilIdle(); coVerify(exactly=2){dao.eliminarPorId(7)}
  coEvery { api.eliminarPreChequeo(7) } throws http(500); vm.eliminar(7); advanceUntilIdle(); coEvery { api.eliminarPreChequeo(7) } throws IOException(); vm.eliminar(7); advanceUntilIdle(); coVerify(exactly=2){dao.eliminarPorId(7)}
 }
 @Test fun evidencias()=runTest {
  val vm=HistorialViewModel(app); advanceUntilIdle(); val e=EvidenciaEquipoDto(3,"maleta_aire","Maleta",true,null,null,null,"7","Buzo")
  coEvery { api.listarEvidenciasPorFaena("7") } returns listOf(e); vm.cargarEvidencias(7); advanceUntilIdle(); assertTrue(vm.evidenciasConsultadas); val item=vm.evidenciasServidor.single()
  vm.actualizarEvidencia(item.copy(id=null)); vm.eliminarEvidencia(item.copy(id=null)); coVerify(exactly=0){api.actualizarEvidencia(any(),any())}
  coEvery { api.actualizarEvidencia(3,any()) } returns e.copy(responsable="Otro"); vm.actualizarEvidencia(item); advanceUntilIdle(); assertEquals("Otro",vm.evidenciasServidor.single().responsable)
  coEvery { api.actualizarEvidencia(3,any()) } throws IOException(); vm.actualizarEvidencia(item); advanceUntilIdle()
  coEvery { api.eliminarEvidencia(3) } throws http(500); vm.eliminarEvidencia(item); advanceUntilIdle(); assertEquals(1,vm.evidenciasServidor.size)
  coEvery { api.eliminarEvidencia(3) } throws IOException(); vm.eliminarEvidencia(item); advanceUntilIdle(); assertEquals(1,vm.evidenciasServidor.size)
  coEvery { api.eliminarEvidencia(3) } throws http(404); vm.eliminarEvidencia(item); advanceUntilIdle(); assertTrue(vm.evidenciasServidor.isEmpty())
  vm.cargarEvidencias(7); advanceUntilIdle(); coEvery { api.eliminarEvidencia(3) } returns Unit; vm.eliminarEvidencia(item); advanceUntilIdle(); assertTrue(vm.evidenciasServidor.isEmpty())
  coEvery { api.listarEvidenciasPorFaena("7") } throws IOException(); vm.cargarEvidencias(7); advanceUntilIdle(); assertFalse(vm.evidenciasConsultadas)
 }
}
