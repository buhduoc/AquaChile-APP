package com.duoc.aquachileapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aquachileapp.data.local.AppDatabase
import com.duoc.aquachileapp.data.local.toDomain
import com.duoc.aquachileapp.data.local.toEntity
import com.duoc.aquachileapp.model.CHECKLIST
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.obtenerInstancia(application).preChequeoDao()

    val registros = mutableStateListOf<PreChequeo>()

    init {
        viewModelScope.launch {
            val initial = dao.obtenerTodosSync()
            if (initial.isEmpty()) {
                val demo1 = ejemplo(
                    id = 1,
                    centro = "Centro Calbuco",
                    fecha = "17-10-2026 07:45",
                    buzo = "Carlos Muñoz",
                    items = itemsDeEjemplo()
                )
                val demo2 = ejemplo(
                    id = 2,
                    centro = "Centro Osorno",
                    fecha = "16-10-2026 14:10",
                    buzo = "Andrés Vera",
                    items = itemsDeEjemplo(observada = true),
                    observaciones = "Señalización con poca visibilidad"
                )
                val demo3 = ejemplo(
                    id = 3,
                    centro = "Centro Puerto Montt",
                    fecha = "15-10-2026 09:00",
                    buzo = "Luis Barría",
                    items = itemsDeEjemplo(fallaCritica = true),
                    observaciones = "Compresor sin presión"
                )
                dao.insertar(demo1.toEntity())
                dao.insertar(demo2.toEntity())
                dao.insertar(demo3.toEntity())
            }

            dao.obtenerTodos().collectLatest { entities ->
                registros.clear()
                registros.addAll(entities.map { it.toDomain() })
            }
        }
    }

    fun agregar(preChequeo: PreChequeo) {
        viewModelScope.launch {
            dao.insertar(preChequeo.toEntity())
        }
    }

    fun buscar(id: Int): PreChequeo? {
        return registros.firstOrNull { it.id == id }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            dao.eliminarPorId(id)
        }
    }

    fun eliminar(preChequeo: PreChequeo) {
        viewModelScope.launch {
            dao.eliminar(preChequeo.toEntity())
        }
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
    }

    var centro by mutableStateOf("")
    var buzo by mutableStateOf("")

    fun guardar(
        items: List<ItemChecklist>,
        supervisor: String,
        observaciones: String = "",
        fotos: List<String> = emptyList()
    ) {
        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        val nuevoPreChequeo = PreChequeo(
            id = 0,
            centro = centro.ifBlank { "Centro Puerto Montt" },
            fecha = fecha,
            buzo = buzo.ifBlank { "Sin buzo" },
            supervisor = supervisor,
            items = items,
            observaciones = observaciones,
            fotos = fotos,
            sistolica = 120,
            diastolica = 80,
            saturacion = 98,
            alertas = emptyList()
        )

        viewModelScope.launch {
            dao.insertar(nuevoPreChequeo.toEntity())
        }

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
