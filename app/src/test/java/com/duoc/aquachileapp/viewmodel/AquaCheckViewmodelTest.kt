package com.duoc.aquachileapp.viewmodel

import com.duoc.aquachileapp.model.*
import org.junit.Assert.*
import org.junit.Test
import io.mockk.*

class AquaCheckViewmodelTest {
    @Test fun estadoInicial() {
        val vm = AquaCheckViewmodel()
        assertEquals(6, vm.checklist.size)
        assertFalse(vm.completo)
        assertFalse(vm.requiereObservacion)
        assertFalse(vm.puedeGuardar)
        assertTrue(vm.adjuntos.isEmpty())
    }
    @Test fun checklistCompleto() {
        val vm = AquaCheckViewmodel()
        vm.checklist.indices.forEach { vm.cambiarEstado(it, Estado.CUMPLE) }
        assertTrue(vm.completo)
        assertTrue(vm.puedeGuardar)
        for (estado in listOf(Estado.NO_CUMPLE, Estado.OBSERVADO)) {
            vm.cambiarEstado(0, estado)
            vm.observaciones = " "
            assertTrue(vm.requiereObservacion)
            assertFalse(vm.puedeGuardar)
            vm.observaciones = "Revisar equipo"
            assertTrue(vm.puedeGuardar)
        }
    }
    @Test fun adjuntosSinDuplicados() {
        val vm = AquaCheckViewmodel()
        vm.agregarAdjunto("foto1")
        vm.agregarAdjunto("foto1")
        vm.agregarAdjunto("foto2")
        assertEquals(listOf("foto1", "foto2"), vm.adjuntos.toList())
        vm.quitarAdjunto(0)
        assertEquals(listOf("foto2"), vm.adjuntos.toList())
    }
}
