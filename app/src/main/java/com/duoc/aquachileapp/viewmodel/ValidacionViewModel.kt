package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.domain.Reglas
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ResultadoValidacion

class ValidacionViewModel : ViewModel() {

    var edad by mutableStateOf("48")
    var pesoKg by mutableStateOf("82.0")
    var alturaCm by mutableStateOf("172.0")

    var examenPreocupacionalVigente by mutableStateOf(true)
    var testEsfuerzoVigente by mutableStateOf(false)
    var puntajeEscuelaCalbuco by mutableStateOf("88")

    var sistolica by mutableStateOf("128")
    var diastolica by mutableStateOf("82")
    var saturacion by mutableStateOf("97")

    val alertasSalud = mutableStateListOf<String>()
    val itemsChecklist = mutableStateListOf(*CHECKLIST.toTypedArray())

    val resultadoValidacion: ResultadoValidacion
        get() {
            val edadNum = edad.toIntOrNull() ?: 30
            val pesoNum = pesoKg.toDoubleOrNull() ?: 70.0
            val alturaNum = alturaCm.toDoubleOrNull() ?: 170.0
            val puntajeNum = puntajeEscuelaCalbuco.toIntOrNull() ?: 80
            val sisNum = sistolica.toIntOrNull() ?: 120
            val diaNum = diastolica.toIntOrNull() ?: 80
            val satNum = saturacion.toIntOrNull() ?: 98

            return Reglas.evaluarValidacion(
                edad = edadNum,
                pesoKg = pesoNum,
                alturaCm = alturaNum,
                examenPreocupacionalVigente = examenPreocupacionalVigente,
                testEsfuerzoVigente = testEsfuerzoVigente,
                puntajeEscuelaCalbuco = puntajeNum,
                sistolica = sisNum,
                diastolica = diaNum,
                saturacion = satNum,
                alertasSalud = alertasSalud.toList(),
                itemsChecklist = itemsChecklist.toList()
            )
        }

    fun alternarAlerta(pregunta: String) {
        if (pregunta in alertasSalud) {
            alertasSalud.remove(pregunta)
        } else {
            alertasSalud.add(pregunta)
        }
    }

    fun cambiarEstadoItem(indice: Int, estado: Estado) {
        if (indice in itemsChecklist.indices) {
            itemsChecklist[indice] = itemsChecklist[indice].copy(estado = estado)
        }
    }
}
