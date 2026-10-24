package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.ui.components.AzulLogin
import com.duoc.aquachileapp.ui.components.FondoLogin
import com.duoc.aquachileapp.ui.components.RosaError
import com.duoc.aquachileapp.ui.components.coloresCampoLogin
import com.duoc.aquachileapp.viewmodel.CuentasViewModel

@Composable
fun RegistroScreen(
    cuentas: CuentasViewModel,
    onVolver: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var enviado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoLogin)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {

        if (enviado) {

            Text(
                text = "Solicitud enviada",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Un supervisor debe aprobar tu cuenta antes de que puedas ingresar.",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVolver,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AzulLogin
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio de sesión")
            }

        } else {

            Text(
                text = "Crear cuenta",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = null
                },
                label = { Text("Nombre completo") },
                singleLine = true,
                colors = coloresCampoLogin(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    error = null
                },
                label = { Text("RUT o correo corporativo") },
                singleLine = true,
                colors = coloresCampoLogin(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = clave,
                onValueChange = {
                    clave = it
                    error = null
                },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = coloresCampoLogin(),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = RosaError
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val resultado = cuentas.registrar(usuario, clave, nombre,rut,telefono,correo)
                    if (resultado == null) {
                        enviado = true
                    } else {
                        error = resultado
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AzulLogin
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar solicitud")
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver", color = Color.White)
            }
        }
    }
}