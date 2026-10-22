package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.aquachileapp.model.PreChequeo
import com.duoc.aquachileapp.model.Resultado
import com.duoc.aquachileapp.ui.components.TarjetaPreChequeo
import com.duoc.aquachileapp.ui.components.colorDe
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.ui.theme.TextoOscuro
import com.duoc.aquachileapp.ui.theme.TextoSuave
import com.duoc.aquachileapp.viewmodel.HistorialViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy")

private fun fechaDe(p: PreChequeo): LocalDate? =
    runCatching { LocalDate.parse(p.fecha.take(10), FORMATO_FECHA) }.getOrNull()

@Composable
fun ResumenScreen(
    historial: HistorialViewModel,
    onDetalle: (Int) -> Unit
) {
    val registros = historial.registros

    var mesTxt by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
    var diaTxt by rememberSaveable { mutableStateOf<String?>(null) }

    val mes = YearMonth.parse(mesTxt)
    val diaSel = diaTxt?.let { LocalDate.parse(it) }

    val porDia: Map<LocalDate, List<PreChequeo>> = registros
        .mapNotNull { r -> fechaDe(r)?.let { it to r } }
        .groupBy({ it.first }, { it.second })

    val lista = if (diaSel != null) porDia[diaSel].orEmpty() else registros.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------- 2) Calendario ----------
        Calendario(
            mes = mes,
            diaSel = diaSel,
            porDia = porDia,
            onMes = { mesTxt = it.toString() },
            onDia = { dia ->
                diaTxt = if (dia == diaSel) null else dia.toString()
            }
        )

        // ---------- 3) Gráfico ----------
        GraficoReportes(porDia = porDia, registros = registros)

        // ---------- Lista de reportes ----------
        Text(
            text = if (diaSel != null) "Reportes del ${diaSel.format(FORMATO_FECHA)}" else "Últimos reportes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        if (lista.isEmpty()) {
            Text(
                text = if (diaSel != null) "No hay pre-chequeos en este día" else "Aún no hay pre-chequeos guardados",
                fontSize = 13.sp,
                color = TextoSuave
            )
        }

        lista.forEach { preChequeo ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetalle(preChequeo.id) }
            ) {
                TarjetaPreChequeo(preChequeo)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TarjetaBlanca(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

// ======================= CALENDARIO =======================

@Composable
private fun Calendario(
    mes: YearMonth,
    diaSel: LocalDate?,
    porDia: Map<LocalDate, List<PreChequeo>>,
    onMes: (YearMonth) -> Unit,
    onDia: (LocalDate) -> Unit
) {
    val titulo = mes.month
        .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-CL"))
        .replaceFirstChar { it.uppercase() } + " de ${mes.year}"

    val hoy = LocalDate.now()
    val desfase = mes.atDay(1).dayOfWeek.value - 1      // lunes = 0
    val totalCeldas = ((desfase + mes.lengthOfMonth() + 6) / 7) * 7

    TarjetaBlanca {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMes(mes.minusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = titulo,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextoOscuro
                )
                IconButton(onClick = { onMes(mes.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }

            HorizontalDivider(color = Color(0xFFECEEF8))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { d ->
                    Text(
                        text = d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = TextoSuave
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            for (fila in 0 until totalCeldas / 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val dia = fila * 7 + col - desfase + 1
                        val celdaModifier = Modifier.weight(1f)
                        if (dia in 1..mes.lengthOfMonth()) {
                            val fecha = mes.atDay(dia)
                            val peor = porDia[fecha]?.maxByOrNull { it.resultado.ordinal }?.resultado
                            CeldaDia(
                                dia = dia,
                                seleccionado = fecha == diaSel,
                                esHoy = fecha == hoy,
                                resultado = peor,
                                onClick = { onDia(fecha) },
                                modifier = celdaModifier
                            )
                        } else {
                            Box(modifier = celdaModifier.aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    dia: Int,
    seleccionado: Boolean,
    esHoy: Boolean,
    resultado: Resultado?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(CircleShape)
            .background(if (seleccionado) Morado else Color.Transparent)
            .then(
                if (esHoy && !seleccionado) Modifier.border(1.dp, Morado, CircleShape) else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$dia",
                fontSize = 13.sp,
                color = if (seleccionado) Color.White else TextoOscuro
            )
            if (resultado != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (seleccionado) Color.White else colorDe(resultado))
                )
            }
        }
    }
}

// ======================= GRÁFICO =======================

@Composable
private fun GraficoReportes(
    porDia: Map<LocalDate, List<PreChequeo>>,
    registros: List<PreChequeo>
) {
    val dias = porDia.toList().sortedBy { it.first.toEpochDay() }.takeLast(7)
    val maximo = (dias.maxOfOrNull { it.second.size } ?: 1).coerceAtLeast(1)
    val alturaMax = 130.dp

    val total = registros.size
    val cumplen = registros.count { it.resultado == Resultado.CUMPLE }
    val porcentaje = if (total == 0) 0 else cumplen * 100 / total

    TarjetaBlanca {
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
                    text = "Últimos reportes",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextoOscuro
                )
                Text(
                    text = "$total en total · $porcentaje% cumple",
                    fontSize = 11.sp,
                    color = TextoSuave
                )
            }

            if (dias.isEmpty()) {
                Text("Aún no hay datos para graficar", fontSize = 13.sp, color = TextoSuave)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(alturaMax + 22.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dias.forEach { (_, lista) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${lista.size}",
                                fontSize = 11.sp,
                                color = TextoOscuro,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(30.dp)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            ) {
                                // arriba lo más grave, abajo lo que cumple
                                Resultado.entries.reversed().forEach { r ->
                                    val cantidad = lista.count { it.resultado == r }
                                    if (cantidad > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(alturaMax * cantidad / maximo)
                                                .background(colorDe(r))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFECEEF8))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dias.forEach { (fecha, _) ->
                        Text(
                            text = "%02d/%02d".format(fecha.dayOfMonth, fecha.monthValue),
                            fontSize = 10.sp,
                            color = TextoSuave,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
                ) {
                    Resultado.entries.forEach { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(colorDe(r))
                            )
                            Text(
                                text = " ${r.texto}",
                                fontSize = 10.sp,
                                color = TextoSuave
                            )
                        }
                    }
                }
            }
        }
    }
}
