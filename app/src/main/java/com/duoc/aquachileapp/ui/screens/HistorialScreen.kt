package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.viewmodel.HistorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    vm: HistorialViewModel
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf<Resultado?>(null) }

    val texto = busqueda.trim()
    val filtrados = vm.registros.filter { registro ->
        val coincideResultado = filtro == null || registro.resultado == filtro
        val coincideTexto = texto.isEmpty() ||
            registro.centro.contains(texto, ignoreCase = true) ||
            registro.buzo.contains(texto, ignoreCase = true) ||
            registro.supervisor.contains(texto, ignoreCase = true) ||
            registro.fecha.contains(texto)
        coincideResultado && coincideTexto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Historial de pre-chequeos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar por centro, buzo, supervisor o fecha") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { busqueda = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtro == null,
                onClick = { filtro = null },
                label = { Text("Todos") }
            )
            Resultado.entries.forEach { resultado ->
                FilterChip(
                    selected = filtro == resultado,
                    onClick = { filtro = if (filtro == resultado) null else resultado },
                    label = { Text(resultado.texto) }
                )
            }
        }

        Text(
            text = "Mostrando ${filtrados.size} de ${vm.registros.size}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (vm.registros.isEmpty()) {
            Text("Aún no hay pre-chequeos guardados")
        } else if (filtrados.isEmpty()) {
            Text("No hay pre-chequeos que coincidan con la búsqueda o el filtro")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtrados) { preChequeo ->
                TarjetaPreChequeo(preChequeo)
            }
        }
    }
}