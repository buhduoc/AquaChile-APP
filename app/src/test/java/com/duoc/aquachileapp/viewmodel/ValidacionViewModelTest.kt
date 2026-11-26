package com.duoc.aquachileapp.viewmodel

import com.duoc.aquachileapp.model.*
import org.junit.Assert.*
import org.junit.Test
import io.mockk.*

class ValidacionViewModelTest {
    private fun normal() = ValidacionViewModel().apply {
        edad = "30"; pesoKg = "70"; alturaCm = "170"
        examenPreocupacionalVigente = true; testEsfuerzoVigente = true
        puntajeEscuelaCalbuco = "90"; sistolica = "120"; diastolica = "80"; saturacion = "98"
        itemsChecklist.indices.forEach { cambiarEstadoItem(it, Estado.CUMPLE) }
    }
    @Test fun parametrosNormales() {
        val vm = normal()
        assertEquals(30, vm.edad.toInt())
        assertFalse(vm.resultadoValidacion.estaBloqueado)
        assertEquals(SemaforoRiesgo.VERDE, vm.resultadoValidacion.semaforo)
        assertEquals(24.2, vm.resultadoValidacion.imc, 0.01)
    }
    @Test fun alertaAlternable() {
        val vm = normal()
        vm.alternarAlerta("Alerta de prueba")
        assertEquals(listOf("Alerta de prueba"), vm.alertasSalud.toList())
        assertEquals(SemaforoRiesgo.AMARILLO, vm.resultadoValidacion.semaforo)
        vm.alternarAlerta("Alerta de prueba")
        assertTrue(vm.alertasSalud.isEmpty())
        assertEquals(SemaforoRiesgo.VERDE, vm.resultadoValidacion.semaforo)
    }
    @Test fun incumplimientoCritico() {
        val vm = normal()
        val indice = vm.itemsChecklist.indexOfFirst { it.critico }
        vm.cambiarEstadoItem(indice, Estado.NO_CUMPLE)
        assertTrue(vm.resultadoValidacion.estaBloqueado)
        assertEquals(SemaforoRiesgo.ROJO, vm.resultadoValidacion.semaforo)
        assertTrue(vm.resultadoValidacion.motivosBloqueo.any { it.contains(vm.itemsChecklist[indice].nombre) })
        val antes = vm.itemsChecklist.toList()
        vm.cambiarEstadoItem(-1, Estado.CUMPLE)
        vm.cambiarEstadoItem(antes.size, Estado.CUMPLE)
        assertEquals(antes, vm.itemsChecklist.toList())
    }
    @Test fun examenYParametrosCriticos() {
        val vm = normal()
        vm.examenPreocupacionalVigente = false
        assertTrue(vm.resultadoValidacion.estaBloqueado)
        vm.examenPreocupacionalVigente = true
        vm.saturacion = "85"
        assertTrue(vm.resultadoValidacion.estaBloqueado)
        vm.saturacion = "98"
        vm.edad = "48"; vm.testEsfuerzoVigente = false
        assertTrue(vm.resultadoValidacion.estaBloqueado)
    }
    @Test fun entradasInvalidasNoLanzanExcepcion() {
        val vm = normal()
        vm.edad = ""; vm.pesoKg = "x"; vm.alturaCm = "x"
        vm.puntajeEscuelaCalbuco = "x"; vm.sistolica = "x"; vm.diastolica = "x"; vm.saturacion = "x"
        assertEquals(24.2, vm.resultadoValidacion.imc, 0.01)
    }
}
