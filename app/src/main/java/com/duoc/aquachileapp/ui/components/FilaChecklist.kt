package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilaChecklist(
    item: ItemChecklist,
    alElegir: (Estado) -> Unit
) {
    Column {

        Text(
            if (item.critico) "${item.nombre} (crítico)" else item.nombre
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Estado.entries.filter { it != Estado.PENDIENTE }.forEach { estado ->
                FilterChip(
                    selected = item.estado == estado,
                    onClick = { alElegir(estado) },
                    label = { Text(estado.texto) }
                )
            }
        }
    }
}
