
package com.duoc.aquachileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoSuave

fun colorDe(resultado: Resultado): Color {
    return when (resultado) {
        Resultado.CUMPLE -> Color(0xFF2E9E5B)
        Resultado.OBSERVADO -> Color(0xFFE5A100)
        Resultado.REQUIERE_REVISION -> Color(0xFFE03E3E)
    }
}

@Composable
fun EtiquetaResultado(resultado: Resultado) {
    Surface(
        color = colorDe(resultado),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "Evaluación local: ${resultado.texto}",
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 3.dp
            )
        )
    }
}

@Composable
fun TarjetaPreChequeo(
    preChequeo: PreChequeo,
    estadoSincronizacion: String? = null
) {

    // Detectar el estado actual de la conexión.
    val sinInternet = recordarSinInternet()

    // Cambiar el color según la conexión.
    val colorPrincipal = if (sinInternet) {
        Color(0xFF64748B)
    } else {
        Morado
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Franja lateral.
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .background(colorPrincipal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Assignment,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // Información del prechequeo.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                Text(
                    text = "${preChequeo.centro} · ${preChequeo.buzo}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = preChequeo.fecha,
                    fontSize = 11.sp,
                    color = TextoSuave
                )

                Text(
                    text = "Supervisor: ${preChequeo.supervisor}",
                    fontSize = 11.sp,
                    color = TextoSuave
                )

                if (preChequeo.fotos.isNotEmpty()) {
                    Text(
                        text = "Adjuntos: ${preChequeo.fotos.size}",
                        fontSize = 11.sp,
                        color = TextoSuave
                    )
                }

                EtiquetaResultado(preChequeo.resultado)
                Text("Sin autorización definitiva", fontSize = 10.sp, color = TextoSuave)

                // Estado de sincronización.
                when (estadoSincronizacion) {

                    "PENDIENTE",
                    "ERROR",
                    "SINCRONIZANDO" -> {

                        Surface(
                            color = Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Guardado local · Pendiente de sincronizar",
                                color = Color(0xFF334155),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 5.dp
                                )
                            )
                        }
                    }

                    "SINCRONIZADO" -> {
                        Text(
                            text = "Sincronizado",
                            fontSize = 10.sp,
                            color = Color(0xFF15803D)
                        )
                    }

                    "LEGADO_REVISAR" -> {
                        Text(
                            text = "Estado de sincronización por revisar",
                            fontSize = 10.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // Icono de opciones.
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}
