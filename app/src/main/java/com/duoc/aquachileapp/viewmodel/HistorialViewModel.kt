package com.duoc.aquachileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo

class HistorialViewModel : ViewModel() {

    val registros = mutableStateListOf(
        ejemplo(
            id = 3,
            centro = "Centro Calbuco",
            fecha = "17-10-2026 07:45",
            buzo = "Carlos Muñoz",
            items = itemsDeEjemplo()
        ),
        ejemplo(
            id = 2,
            centro = "Centro Hornopirén",
            fecha = "16-10-2026 14:10",
            buzo = "Andrés Vera",
            items = itemsDeEjemplo(observada = true),
            observaciones = "Señalización con poca visibilidad"
        ),
        ejemplo(
            id = 1,
            centro = "Centro Quellón",
            fecha = "15-10-2026 09:00",
            buzo = "Luis Barría",
            items = itemsDeEjemplo(fallaCritica = true),
            observaciones = "Compresor sin presión"
        )
    )

    fun agregar(preChequeo: PreChequeo) {
        registros.add(0, preChequeo.copy(id = registros.size + 1))
    }

    fun buscar(id: Int): PreChequeo? {
        return registros.firstOrNull { it.id == id }
    }

    private fun ejemplo(
        id: Int,
        centro: String,
        fecha: String,
        buzo: String,
        items: List<ItemChecklist>,
        observaciones: String = ""
    ): PreChequeo {
        return PreChequeo(
            id = id,
            centro = centro,
            fecha = fecha,
            buzo = buzo,
            supervisor = "Supervisor Demo",
            items = items,
            observaciones = observaciones,
            fotos = emptyList(),
            sistolica = 120,
            diastolica = 80,
            saturacion = 98,
            alertas = emptyList()
        )
    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")

        fun guardar(
        items: List<ItemChecklist>,
        supervisor: String,
        observaciones: String = "",
        fotos: List<String> = emptyList()
    ) {
        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        registros.add(
            0,
            PreChequeo(
                id = registros.size + 1,
                centro = centro.ifBlank { "Sin centro" },
                fecha = fecha,
                buzo = buzo.ifBlank { "Sin buzo" },
                supervisor = supervisor,
                items = items,
                observaciones = observaciones,
                fotos = fotos
            )
        )

        centro = ""
        buzo = ""
    }

    private fun itemsDeEjemplo(
        fallaCritica: Boolean = false,
        observada: Boolean = false
    ): List<ItemChecklist> {
        return CHECKLIST.mapIndexed { indice, item ->
            when {
                fallaCritica && indice == 1 -> item.copy(estado = Estado.NO_CUMPLE)
                observada && indice == 4 -> item.copy(estado = Estado.OBSERVADO)
                else -> item.copy(estado = Estado.CUMPLE)
            }
        }
    }
}
