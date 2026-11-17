package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.aquachileapp.R
import com.duoc.aquachileapp.domain.Reglas
import com.duoc.aquachileapp.model.SemaforoRiesgo
import com.duoc.aquachileapp.model.Usuario
import com.duoc.aquachileapp.ui.components.Campo
import com.duoc.aquachileapp.ui.theme.Morado
import com.duoc.aquachileapp.viewmodel.CuentasViewModel

@Composable
fun PerfilScreen(
    usuario: Usuario?,
    cuentas: CuentasViewModel? = null,
    onActualizarPerfil: ((Usuario) -> Unit)? = null
) {
    val nombre = usuario?.nombre ?: "Usuario"
    val rol = usuario?.rol ?: "Sin rol"
    val rut = usuario?.rut ?: "Sin rut"
    val telefono = usuario?.telefono ?: "Sin telefono"
    val correo = usuario?.correo ?: "Sin correo"

    // Datos biométricos y de acreditaciones
    var edadStr by remember(usuario) { mutableStateOf((usuario?.edad ?: 45).toString()) }
    var pesoStr by remember(usuario) { mutableStateOf((usuario?.pesoKg ?: 80.0).toString()) }
    var alturaStr by remember(usuario) { mutableStateOf((usuario?.alturaCm ?: 172.0).toString()) }
    var examenVigente by remember(usuario) { mutableStateOf(usuario?.examenPreocupacionalVigente ?: true) }
    var testVigente by remember(usuario) { mutableStateOf(usuario?.testEsfuerzoVigente ?: false) }
    var puntajeCalbucoStr by remember(usuario) { mutableStateOf((usuario?.puntajeEscuelaCalbuco ?: 85).toString()) }

    var mensajeGuardado by remember { mutableStateOf<String?>(null) }

    // Evaluación en vivo del perfil
    val edadNum = edadStr.toIntOrNull() ?: 45
    val pesoNum = pesoStr.toDoubleOrNull() ?: 80.0
    val alturaNum = alturaStr.toDoubleOrNull() ?: 172.0
    val puntajeNum = puntajeCalbucoStr.toIntOrNull() ?: 85

    val valPerfil = Reglas.evaluarValidacion(
        edad = edadNum,
        pesoKg = pesoNum,
        alturaCm = alturaNum,
        examenPreocupacionalVigente = examenVigente,
        testEsfuerzoVigente = testVigente,
        puntajeEscuelaCalbuco = puntajeNum,
        sistolica = 120,
        diastolica = 80,
        saturacion = 98,
        alertasSalud = emptyList(),
        itemsChecklist = emptyList()
    )

    val fotoPerfil = if (usuario?.esSupervisor == true) {
        R.drawable.supervisor_demo
    } else {
        R.drawable.buzo_demo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Image(
            painter = painterResource(id = fotoPerfil),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF07152F)
            )

            Text(
                text = rol,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4F5665)
            )
        }

        // Card Información personal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Información del usuario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )

                    Box(
                        modifier = Modifier
                            .background(color = Color(0xFFBDF5E7), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Perfil Activo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF168C79)
                        )
                    }
                }

                DatoPerfil(titulo = "Nombre", valor = nombre)
                SeparadorPerfil()
                DatoPerfil(titulo = "Rol", valor = rol)
                SeparadorPerfil()
                DatoPerfil(titulo = "RUT", valor = rut)
                SeparadorPerfil()
                DatoPerfil(titulo = "Teléfono", valor = telefono)
                SeparadorPerfil()
                DatoPerfil(titulo = "Correo", valor = correo)
            }
        }

        // Card Datos Biométricos y Acreditaciones de Buceo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Datos Biométricos y Acreditaciones de Buceo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Campo(edadStr, { edadStr = it; mensajeGuardado = null }, "Edad (años)", Modifier.weight(1f), numero = true)
                    Campo(pesoStr, { pesoStr = it; mensajeGuardado = null }, "Peso (kg)", Modifier.weight(1f), numero = true)
                    Campo(alturaStr, { alturaStr = it; mensajeGuardado = null }, "Altura (cm)", Modifier.weight(1f), numero = true)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Examen Preocupacional Vigente", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = examenVigente,
                                onCheckedChange = { examenVigente = it; mensajeGuardado = null },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Morado)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Test de Esfuerzo Vigente (>45 años)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = testVigente,
                                onCheckedChange = { testVigente = it; mensajeGuardado = null },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Morado)
                            )
                        }

                        Campo(
                            valor = puntajeCalbucoStr,
                            alCambiar = { puntajeCalbucoStr = it; mensajeGuardado = null },
                            etiqueta = "Puntaje Escuela Buceo Calbuco (0-100 pts)",
                            numero = true
                        )
                    }
                }

                // Tarjeta Semáforo del Perfil
                Card(
                    colors = CardDefaults.cardColors(containerColor = valPerfil.semaforo.color.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (valPerfil.estaBloqueado) Icons.Default.Lock else if (valPerfil.semaforo == SemaforoRiesgo.AMARILLO) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = valPerfil.semaforo.color,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Semáforo Perfil: ${valPerfil.semaforo.titulo}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = valPerfil.semaforo.color
                            )
                        }

                        Text(
                            text = "IMC: ${valPerfil.imc} kg/m² (${valPerfil.imcCategoria})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF374151)
                        )

                        if (valPerfil.estaBloqueado) {
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("⛔ REQUISITOS DE PERFIL INCUMPLIDOS:", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    valPerfil.motivosBloqueo.forEach { m ->
                                        Text("• $m", color = Color(0xFF991B1B), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (usuario != null) {
                            val usuarioAct = usuario.copy(
                                edad = edadNum,
                                pesoKg = pesoNum,
                                alturaCm = alturaNum,
                                examenPreocupacionalVigente = examenVigente,
                                testEsfuerzoVigente = testVigente,
                                puntajeEscuelaCalbuco = puntajeNum
                            )
                            cuentas?.actualizarBiometricos(
                                usuarioId = usuario.correo.ifBlank { usuario.rut },
                                edad = edadNum,
                                pesoKg = pesoNum,
                                alturaCm = alturaNum,
                                examenVigente = examenVigente,
                                testVigente = testVigente,
                                puntajeCalbuco = puntajeNum
                            )
                            onActualizarPerfil?.invoke(usuarioAct)
                            mensajeGuardado = "Acreditaciones de perfil actualizadas correctamente"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Guardar Biométricos de Perfil", fontWeight = FontWeight.Bold)
                }

                mensajeGuardado?.let { msj ->
                    Text(
                        text = msj,
                        color = Color(0xFF047857),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DatoPerfil(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF707784)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B5563)
        )
    }
}

@Composable
fun SeparadorPerfil() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF0F1F4))
    )
}
