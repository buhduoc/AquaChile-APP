package com.duoc.aquachileapp.viewmodel
import com.duoc.aquachileapp.model.*
import org.junit.Test
import org.junit.Assert.*
class NuevoViewModelTest {
 private fun listo() = NuevoViewModel().apply { centro="Centro"; buzo="Buzo"; supervisor="Supervisor"; items.indices.forEach { cambiarEstado(it,Estado.CUMPLE) } }
 @Test fun validacionesFormulario() {
  val vm=NuevoViewModel(); assertNull(vm.crear()); assertEquals("Completa el centro, el buzo y el supervisor",vm.error)
  vm.centro="Centro"; vm.buzo="Buzo"; vm.supervisor="Supervisor"; assertNull(vm.crear()); assertEquals("Evalúa todos los ítems del checklist",vm.error)
  vm.items.indices.forEach { vm.cambiarEstado(it,Estado.CUMPLE) }; vm.cambiarEstado(0,Estado.NO_CUMPLE); assertNull(vm.crear()); assertEquals("Escribe una observación que justifique",vm.error)
  vm.observaciones="Falla"; assertTrue(vm.validacion.estaBloqueado); assertNull(vm.crear()); assertTrue(vm.error!!.contains("Aprobación bloqueada"))
  vm.cambiarEstado(0,Estado.CUMPLE); vm.sistolica="x"; assertNull(vm.crear()); assertEquals("Completa presión y saturación",vm.error); vm.sistolica="120"; vm.saturacion="101"; assertNull(vm.crear()); assertEquals("Valores de salud fuera de rango",vm.error)
 }
 @Test fun crearEditarLimpiar() {
  val vm=listo(); vm.cambiarObservacion(0,"Revisado"); vm.agregarFotoItem(0,"foto"); vm.marcarAlerta("Alerta"); val p=vm.crear()!!.copy(id=9); assertEquals("Buzo",p.buzo); assertEquals(listOf("foto"),p.fotos)
  val otro=NuevoViewModel(); otro.cargarParaEditar(p); assertEquals(9,otro.idEnEdicion); assertEquals(p,otro.crear()); otro.limpiar(); assertNull(otro.idEnEdicion); assertTrue(otro.fotos.isEmpty()); assertTrue(otro.alertas.isEmpty()); assertEquals(CHECKLIST,otro.items.toList())
 }
 @Test fun fotosYAlertas() {
  val vm=listo(); val id=vm.evidenciasEquipos.first().idEquipo; vm.adjuntarFotoEquipo(id,"equipo"); vm.adjuntarFotoEquipo(id,"equipo"); assertEquals(listOf("equipo"),vm.fotos.toList()); vm.eliminarFotoEquipo(id); assertTrue(vm.fotos.isEmpty()); vm.eliminarFotoEquipo(id); vm.adjuntarFotoEquipo("inexistente","x"); vm.eliminarFotoEquipo("inexistente")
  vm.agregarFotoItem(0,"a"); vm.agregarFotoItem(0,"a"); vm.agregarFotoItem(1,"a"); vm.eliminarFotoItem(0,0); assertTrue("a" in vm.fotos); vm.eliminarFotoItem(1,0); assertFalse("a" in vm.fotos); vm.eliminarFotoItem(-1,0); vm.eliminarFotoItem(0,99)
  vm.marcarAlerta("A"); assertEquals(NivelSalud.ATENCION,vm.salud); vm.marcarAlerta("A"); assertEquals(NivelSalud.NORMAL,vm.salud)
 }
 @Test fun perfilYSalud() {
  val vm=NuevoViewModel(); vm.prepararDatos(null); assertEquals("Centro Puerto Montt",vm.centro)
  val u=Usuario("Persona","Buzo","123","a@b.cl","456",35,70.0,175.0,true,true,90); vm.prepararDatos(u); assertEquals("Persona",vm.buzo); assertEquals("35",vm.edad); vm.prepararDatos(u.copy(nombre="Otro",rol="Supervisor")); assertEquals("Otro",vm.supervisor)
  vm.sistolica="x"; assertNull(vm.salud); vm.sistolica="120"; vm.diastolica="x"; assertNull(vm.salud); vm.diastolica="80"; vm.saturacion="x"; assertNull(vm.salud); vm.edad="x"; vm.pesoKg="x"; vm.alturaCm="x"; vm.puntajeEscuelaCalbuco="x"; assertFalse(vm.validacion.estaBloqueado)
 }
}
