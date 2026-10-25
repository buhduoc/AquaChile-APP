package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.R
import com.duoc.aquachileapp.model.Usuario
import com.duoc.aquachileapp.ui.components.AzulLogin
import com.duoc.aquachileapp.ui.components.FondoLogin
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    cuentas: CuentasViewModel,
    onLoginExitoso: (Usuario) -> Unit,
    onCrearCuenta: () -> Unit,
    vm: LoginViewModel = viewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoLogin)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aquachile_logo),
                        contentDescription = "Logo AquaChile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AquaChile APP",
                        color = AzulLogin,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = vm.usuario,
                    onValueChange = { vm.onUsuarioChange(it) },
                    label = { Text("RUT o correo corporativo") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vm.contrasena,
                    onValueChange = { vm.onContrasenaChange(it) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (vm.error != null) {
                    Text(
                        text = vm.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val usuario = vm.iniciarSesion(cuentas)
                        if (usuario != null) {
                            onLoginExitoso(usuario)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulLogin,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onCrearCuenta,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear cuenta", color = AzulLogin, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
