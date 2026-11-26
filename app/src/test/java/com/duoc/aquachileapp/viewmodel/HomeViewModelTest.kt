package com.duoc.aquachileapp.viewmodel

import com.duoc.aquachileapp.model.*
import org.junit.Assert.*
import org.junit.Test
import io.mockk.*

class HomeViewModelTest {
    @Test fun navegacion() {
        val vm = HomeViewModel()
        assertEquals(AppSection.PRECHEQUEO, vm.seccion)
        AppSection.values().forEach {
            vm.seleccionar(it)
            assertEquals(it, vm.seccion)
        }
    }
    @Test fun sesion() {
        val vm = HomeViewModel()
        val usuario = Usuario("Prueba", "Buzo", "11111111-1", "test@example.com", "123")
        assertNull(vm.usuario)
        vm.iniciarSesion(usuario)
        assertEquals(usuario, vm.usuario)
        vm.cerrarSesion()
        assertNull(vm.usuario)
    }
}
