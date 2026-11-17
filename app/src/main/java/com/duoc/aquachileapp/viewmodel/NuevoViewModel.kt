package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.R
import com.duoc.aquachileapp.domain.Reglas
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.EvidenciaEquipo
import com.duoc.aquachileapp.model.NivelSalud
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.ResultadoValidacion
import com.duoc.aquachileapp.model.Usuario
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NuevoViewModel : ViewModel() {

    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")
    var supervisor by mutableStateOf("")

    val items = CHECKLIST.toMutableStateList()

    var observaciones by mutableStateOf("")

    val fotos = mutableStateListOf<String>()

    val evidenciasEquipos = mutableStateListOf(
        EvidenciaEquipo("maleta_aire", "Maleta de Suministro de Aire", resFotoReferencia = R.drawable.maleta_suministro),
        EvidenciaEquipo("filtros_compresor", "Filtros del Compresor", resFotoReferencia = R.drawable.filtros_compresor),
        EvidenciaEquipo("aletas_buceo", "Aletas de Buceo", resFotoReferencia = R.drawable.aletas),
        EvidenciaEquipo("dispositivo_census", "Dispositivo Census", resFotoReferencia = R.drawable.dispositivo_census)
    )

    var sistolica by mutableStateOf("120")
    var diastolica by mutableStateOf("80")
    var saturacion by mutableStateOf("98")

    val alertas = mutableStateListOf<String>()

    var edad by mutableStateOf("45")
    var pesoKg by mutableStateOf("80.0")
    var alturaCm by mutableStateOf("172.0")

    var examenPreocupacionalVigente by mutableStateOf(true)
    var testEsfuerzoVigente by mutableStateOf(false)
    var puntajeEscuelaCalbuco by mutableStateOf("85")

    var error by mutableStateOf<String?>(null)

    val salud: NivelSalud?
        get() {
            val sis = sistolica.toIntOrNull() ?: return null
            val dia = diastolica.toIntOrNull() ?: return null
            val sat = saturacion.toIntOrNull() ?: return null
            return Reglas.nivelSalud(sis, dia, sat, alertas.size)
        }

    val validacion: ResultadoValidacion
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
                alertasSalud = alertas.toList(),
                itemsChecklist = items.toList()
            )
        }

    fun prepararDatos(usuario: Usuario?) {
        if (centro.isBlank()) centro = "Centro Puerto Montt"
        if (usuario == null) return

        edad = usuario.edad.toString()
        pesoKg = usuario.pesoKg.toString()
        alturaCm = usuario.alturaCm.toString()
        examenPreocupacionalVigente = usuario.examenPreocupacionalVigente
        testEsfuerzoVigente = usuario.testEsfuerzoVigente
        puntajeEscuelaCalbuco = usuario.puntajeEscuelaCalbuco.toString()

        if (usuario.esSupervisor) {
            if (supervisor.isBlank()) supervisor = usuario.nombre
        } else {
            if (buzo.isBlank()) buzo = usuario.nombre
        }
    }

    fun cambiarEstado(indice: Int, estado: Estado) {
        items[indice] = items[indice].copy(estado = estado)
        error = null
    }

    fun cambiarObservacion(indice: Int, observacion: String) {
        items[indice] = items[indice].copy(observacion = observacion)
        error = null
    }

    fun adjuntarFotoEquipo(idEquipo: String, uriString: String) {
        val index = evidenciasEquipos.indexOfFirst { it.idEquipo == idEquipo }
        if (index >= 0) {
            val fechaAhora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            val idFaenaAct = if (centro.isNotBlank()) centro else "Faena Pre-Zarpe"
            val resp = if (buzo.isNotBlank()) buzo else if (supervisor.isNotBlank()) supervisor else "Responsable A cargo"

            evidenciasEquipos[index] = evidenciasEquipos[index].copy(
                uriFotoActual = uriString,
                fechaHora = fechaAhora,
                idFaena = idFaenaAct,
                responsable = resp
            )
            if (uriString !in fotos) {
                fotos.add(uriString)
            }
        }
    }

    fun eliminarFotoEquipo(idEquipo: String) {
        val index = evidenciasEquipos.indexOfFirst { it.idEquipo == idEquipo }
        if (index >= 0) {
            val uriAnterior = evidenciasEquipos[index].uriFotoActual
            evidenciasEquipos[index] = evidenciasEquipos[index].copy(
                uriFotoActual = null,
                fechaHora = null,
                idFaena = null,
                responsable = null
            )
            if (uriAnterior != null && evidenciasEquipos.none { it.uriFotoActual == uriAnterior }) {
                fotos.remove(uriAnterior)
            }
        }
    }

    fun agregarFotoItem(indice: Int, uri: String) {
        if (indice in items.indices) {
            val lista = items[indice].fotos.toMutableList()
            if (uri !in lista) {
                lista.add(uri)
                items[indice] = items[indice].copy(fotos = lista)
            }
        }
        if (uri !in fotos) {
            fotos.add(uri)
        }
    }

    fun eliminarFotoItem(indiceItem: Int, indiceFoto: Int) {
        if (indiceItem in items.indices) {
            val lista = items[indiceItem].fotos.toMutableList()
            if (indiceFoto in lista.indices) {
                val fotoEliminada = lista.removeAt(indiceFoto)
                items[indiceItem] = items[indiceItem].copy(fotos = lista)
                if (items.none { fotoEliminada in it.fotos }) {
                    fotos.remove(fotoEliminada)
                }
            }
        }
    }

    fun marcarAlerta(pregunta: String) {
        if (pregunta in alertas) {
            alertas.remove(pregunta)
        } else {
            alertas.add(pregunta)
        }
    }

    fun crear(): PreChequeo? {
        val sis = sistolica.toIntOrNull()
        val dia = diastolica.toIntOrNull()
        val sat = saturacion.toIntOrNull()
        val hayObservacion = items.any {
            it.estado == Estado.NO_CUMPLE || it.estado == Estado.OBSERVADO
        }

        error = when {
            centro.isBlank() || buzo.isBlank() || supervisor.isBlank() ->
                "Completa el centro, el buzo y el supervisor"
            items.any { it.estado == Estado.PENDIENTE } ->
                "Evalúa todos los ítems del checklist"
            hayObservacion && observaciones.isBlank() ->
                "Escribe una observación que justifique"
            sis == null || dia == null || sat == null ->
                "Completa presión y saturación"
            sat !in 50..100 || sis !in 70..250 || dia !in 40..150 ->
                "Valores de salud fuera de rango"
            validacion.estaBloqueado ->
                "Aprobación bloqueada por requisitos de perfil o equipos: ${validacion.motivosBloqueo.firstOrNull()}"
            else -> null
        }

        if (error != null || sis == null || dia == null || sat == null) {
            return null
        }

        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        val edadNum = edad.toIntOrNull() ?: 45
        val pesoNum = pesoKg.toDoubleOrNull() ?: 80.0
        val alturaNum = alturaCm.toDoubleOrNull() ?: 172.0
        val puntajeNum = puntajeEscuelaCalbuco.toIntOrNull() ?: 85

        return PreChequeo(
            id = 0,
            centro = centro.trim(),
            fecha = fecha,
            buzo = buzo.trim(),
            supervisor = supervisor.trim(),
            items = items.toList(),
            observaciones = observaciones.trim(),
            fotos = fotos.toList(),
            sistolica = sis,
            diastolica = dia,
            saturacion = sat,
            alertas = alertas.toList(),
            edad = edadNum,
            pesoKg = pesoNum,
            alturaCm = alturaNum,
            examenPreocupacionalVigente = examenPreocupacionalVigente,
            testEsfuerzoVigente = testEsfuerzoVigente,
            puntajeEscuelaCalbuco = puntajeNum,
            evidenciasEquipos = evidenciasEquipos.toList()
        )
    }

    fun limpiar() {
        centro = "Centro Puerto Montt"
        buzo = ""
        supervisor = ""
        items.clear()
        items.addAll(CHECKLIST)
        observaciones = ""
        fotos.clear()
        evidenciasEquipos.clear()
        evidenciasEquipos.addAll(
            listOf(
                EvidenciaEquipo("maleta_aire", "Maleta de Suministro de Aire", resFotoReferencia = R.drawable.maleta_suministro),
                EvidenciaEquipo("filtros_compresor", "Filtros del Compresor", resFotoReferencia = R.drawable.filtros_compresor),
                EvidenciaEquipo("aletas_buceo", "Aletas de Buceo", resFotoReferencia = R.drawable.aletas),
                EvidenciaEquipo("dispositivo_census", "Dispositivo Census", resFotoReferencia = R.drawable.dispositivo_census)
            )
        )
        sistolica = "120"
        diastolica = "80"
        saturacion = "98"
        alertas.clear()
        edad = "45"
        pesoKg = "80.0"
        alturaCm = "172.0"
        examenPreocupacionalVigente = true
        testEsfuerzoVigente = false
        puntajeEscuelaCalbuco = "85"
        error = null
    }
}
