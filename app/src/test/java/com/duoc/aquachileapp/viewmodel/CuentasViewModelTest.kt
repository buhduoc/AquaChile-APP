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
class CuentasViewModelTest {
 private lateinit var api: ApiService
 private val dto = UsuarioDto(7L,"nuevo","clave123","Nuevo","Buzo","123","456","nuevo@example.com",false)
 @Before fun setup() { Dispatchers.setMain(StandardTestDispatcher()); api=mockk(); mockkObject(RetrofitClient); every { RetrofitClient.api } returns api; mockkStatic(Log::class); every { Log.d(any(),any()) } returns 0; every { Log.e(any(),any()) } returns 0 }
 @After fun cleanup() { unmockkObject(RetrofitClient); unmockkStatic(Log::class); Dispatchers.resetMain() }
 @Test fun cargaYBiometria() = runTest {
  val vm=CuentasViewModel()
  coEvery { api.listarUsuarios() } returns listOf(dto, dto.copy(id=8L, usuario="admin", rol="Supervisor", aprobada=true))
  vm.cargarUsuarios(); advanceUntilIdle()
  assertFalse(vm.cargando); assertNull(vm.errorApi); assertEquals(2,vm.cuentas.size)
  assertEquals("Supervisor",vm.cuentas.last().rol); assertEquals(1,vm.pendientes.size)
  vm.actualizarBiometricos("nuevo@example.com",35,70.0,175.0,true,true,90)
  assertEquals(35,vm.cuentas.first().edad)
  vm.actualizarBiometricos("otro",40,80.0,180.0,false,false,70)
  assertEquals(35,vm.cuentas.first().edad)
  vm.actualizarBiometricos(" NUEVO ",36,71.0,175.0,true,true,91)
  assertEquals(36,vm.cuentas.first().edad)
  vm.aprobar(dto.toCuenta().copy(id=null)); vm.eliminar(dto.toCuenta().copy(id=null))
  assertEquals(2,vm.cuentas.size)
 }
 @Test fun errorCarga() = runTest {
  coEvery { api.listarUsuarios() } throws java.io.IOException()
  val vm=CuentasViewModel(); vm.cargarUsuarios(); advanceUntilIdle()
  assertEquals("No se pudo conectar con el servidor",vm.errorApi); assertFalse(vm.cargando)
 }
 @Test fun registroValidaciones() {
  val vm=CuentasViewModel(); val args=listOf("nuevo","clave123","Nuevo","123","456","nuevo@example.com")
  for(i in args.indices) { val a=args.toMutableList(); a[i]=""; assertEquals("Completa todos los campos",vm.registrar(a[0],a[1],a[2],a[3],a[4],a[5])) }
  assertEquals("La contraseña debe tener al menos 6 caracteres",vm.registrar("nuevo","123","Nuevo","123","456","a@b.cl"))
  vm.cuentas.add(dto.toCuenta())
  assertEquals("Ese usuario ya existe",vm.registrar("NUEVO","clave123","Nuevo","123","456","a@b.cl"))
 }
 @Test fun operacionesCorrectas() = runTest {
  val vm=CuentasViewModel(); coEvery { api.registrarUsuario(any()) } returns dto
  assertNull(vm.registrar("nuevo","clave123","Nuevo","123","456","nuevo@example.com")); advanceUntilIdle(); assertEquals(listOf(dto.toCuenta()),vm.pendientes)
  coEvery { api.aprobarUsuario(7L) } returns dto.copy(aprobada=true); vm.aprobar(dto.toCuenta()); advanceUntilIdle(); assertTrue(vm.pendientes.isEmpty()); assertTrue(vm.cuentas.last().aprobada)
  coEvery { api.eliminarUsuario(7L) } returns Unit; vm.eliminar(dto.toCuenta()); advanceUntilIdle(); assertEquals(0,vm.cuentas.size)
  vm.aprobar(dto.toCuenta()); advanceUntilIdle(); assertEquals(0,vm.cuentas.size)
 }
 @Test fun erroresApi() = runTest {
  val vm=CuentasViewModel(); coEvery { api.registrarUsuario(any()) } throws java.io.IOException(); vm.registrar("nuevo","clave123","Nuevo","123","456","nuevo@example.com"); advanceUntilIdle(); assertEquals("No se pudo registrar el usuario",vm.errorApi)
  coEvery { api.aprobarUsuario(7L) } throws java.io.IOException(); vm.aprobar(dto.toCuenta()); advanceUntilIdle(); assertEquals("No se pudo aprobar el usuario",vm.errorApi)
  coEvery { api.eliminarUsuario(7L) } throws java.io.IOException(); vm.eliminar(dto.toCuenta()); advanceUntilIdle(); assertEquals("No se pudo eliminar el usuario",vm.errorApi)
 }
}