package com.duoc.aquachileapp.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.viewmodel.AquaCheckViewmodel


@Composable
fun ChecklistScreen(
    onGuardar: (List<ItemChecklist>) -> Unit,
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

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = if (item.aprobado)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = item.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (item.aprobado)
                                    "Estado: Aprobado"
                                else
                                    "Estado: Pendiente",

                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.cambiarEstado(index)
                            }
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = if (item.aprobado)
                                        Icons.Default.Refresh
                                    else
                                        Icons.Default.Check,

                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                )

                                Text(
                                    if (item.aprobado)
                                        "Cambiar"
                                    else
                                        "Aprobar"
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                onGuardar(viewModel.checklist.toList())
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Guardar pre-chequeo")
        }
    }
}