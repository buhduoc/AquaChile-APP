package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistorialViewModel : ViewModel() {

    val registros = mutableStateListOf(
        PreChequeo(
            id = 3,
            centro = "Centro Calbuco",
            fecha = "17-10-2026 07:45",
            buzo = "Carlos Muñoz",
            supervisor = "Supervisor Demo",
            items = itemsDeEjemplo(compresor = true, oxigeno = true, senalizacion = true)
        ),
        PreChequeo(
            id = 2,
            centro = "Centro Hornopirén",
            fecha = "16-10-2026 14:10",
            buzo = "Andrés Vera",
            supervisor = "Supervisor Demo",
            items = itemsDeEjemplo(compresor = true, oxigeno = true, senalizacion = false)
        ),
        PreChequeo(
            id = 1,
            centro = "Centro Quellón",
            fecha = "15-10-2026 09:00",
            buzo = "Luis Barría",
            supervisor = "Supervisor Demo",
            items = itemsDeEjemplo(compresor = false, oxigeno = true, senalizacion = true)
        )
    )

    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")

    fun guardar(items: List<ItemChecklist>, supervisor: String) {
        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        registros.add(
            0,
            PreChequeo(
                id = registros.size + 1,
                centro = centro.ifBlank { "Sin centro" },
                fecha = fecha,
                buzo = buzo.ifBlank { "Sin buzo" },
                supervisor = supervisor,
                items = items
            )
        )

        centro = ""
        buzo = ""
    }

    private fun itemsDeEjemplo(compresor: Boolean, oxigeno: Boolean, senalizacion: Boolean) = listOf(
        ItemChecklist("Compresor", compresor, critico = true),
        ItemChecklist("Oxigeno", oxigeno, critico = true),
        ItemChecklist("Señalizacion", senalizacion)
    )
}