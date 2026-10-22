package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.aquachileapp.model.Estado
import com.duoc.aquachileapp.model.ItemChecklist

@Composable
fun FilaChecklist(
    item: ItemChecklist,
    alElegir: (Estado) -> Unit,
    alCambiarObservacion: ((String) -> Unit)? = null,
    onTomarFoto: () -> Unit = {},
    onGaleria: () -> Unit = {},
    onEliminarFoto: ((Int) -> Unit)? = null
) {
    val borderColor = when (item.estado) {
        Estado.CUMPLE -> Color(0xFF10B981)
        Estado.OBSERVADO -> Color(0xFFD97706)
        Estado.NO_CUMPLE -> Color(0xFFEF4444)
        else -> Color(0xFFE5E7EB)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f)
                )

                if (item.critico) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Crítico",
                            color = Color(0xFFDC2626),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val opciones = listOf(
                    Estado.CUMPLE to "OK",
                    Estado.OBSERVADO to "Obs.",
                    Estado.NO_CUMPLE to "No",
                    Estado.NO_APLICA to "N/A"
                )

                opciones.forEach { (estado, label) ->
                    val seleccionado = item.estado == estado

                    val chipBg = if (seleccionado) {
                        when (estado) {
                            Estado.CUMPLE -> Color(0xFF10B981)
                            Estado.OBSERVADO -> Color(0xFFD97706)
                            Estado.NO_CUMPLE -> Color(0xFFEF4444)
                            else -> Color(0xFF6B7280)
                        }
                    } else {
                        Color.White
                    }

                    val chipTextColor = if (seleccionado) Color.White else Color(0xFF374151)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = chipBg,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                border = if (!seleccionado) BorderStroke(1.dp, Color(0xFFD1D5DB)) else BorderStroke(0.dp, Color.Transparent),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { alElegir(estado) }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            color = chipTextColor,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            val requiereObservacion = item.estado == Estado.OBSERVADO || item.estado == Estado.NO_CUMPLE
            if (requiereObservacion || item.observacion.isNotEmpty()) {
                val esInvalido = requiereObservacion && item.observacion.trim().length < 5
                OutlinedTextField(
                    value = item.observacion,
                    onValueChange = { alCambiarObservacion?.invoke(it) },
                    label = {
                        Text(
                            if (requiereObservacion) "Observación (obligatoria)" else "Observación"
                        )
                    },
                    supportingText = {
                        if (esInvalido) {
                            Text(
                                "Describe el problema (mín. 5 letras)",
                                color = Color(0xFFDC2626)
                            )
                        }
                    },
                    isError = esInvalido,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (esInvalido) Color(0xFFDC2626) else Color(0xFFD97706),
                        unfocusedBorderColor = if (esInvalido) Color(0xFFEF4444) else Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onTomarFoto,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF0F4C81)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Cámara",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Cámara",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                TextButton(
                    onClick = onGaleria,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF0F4C81)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Galería",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Galería",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (item.fotos.isNotEmpty()) {
                Fotos(
                    fotos = item.fotos,
                    onEliminar = onEliminarFoto
                )
            }
        }
    }
}
