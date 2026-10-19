package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.viewmodel.AquaCheckViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onGuardar: (List<ItemChecklist>, String) -> Unit,
    viewModel: AquaCheckViewmodel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Inspección de Equipos en Terreno",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(viewModel.checklist) { index, item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (item.critico) "${item.nombre} (crítico)" else item.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Estado.entries.forEach { estado ->
                                FilterChip(
                                    selected = item.estado == estado,
                                    onClick = { viewModel.cambiarEstado(index, estado) },
                                    label = { Text(estado.texto) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = viewModel.observaciones,
                    onValueChange = { viewModel.observaciones = it },
                    label = {
                        Text(
                            if (viewModel.requiereObservacion) "Observaciones (obligatorias)"
                            else "Observaciones"
                        )
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (!viewModel.completo) {
            Text(
                text = "Responde todos los ítems para guardar",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { onGuardar(viewModel.checklist.toList(), viewModel.observaciones.trim()) },
            enabled = viewModel.puedeGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Guardar pre-chequeo")
        }
    }
}