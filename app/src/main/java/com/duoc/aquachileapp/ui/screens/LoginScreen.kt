package com.duoc.aquachileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aquachileapp.model.Usuario
import com.duoc.aquachileapp.ui.components.AzulLogin
import com.duoc.aquachileapp.ui.components.FondoLogin
import com.duoc.aquachileapp.ui.components.RosaError
import com.duoc.aquachileapp.ui.components.coloresCampoLogin
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    cuentas: CuentasViewModel,
    onLoginExitoso: (Usuario) -> Unit,
    onCrearCuenta: () -> Unit,
    vm: LoginViewModel = viewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoLogin)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AquaChile APP",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Iniciar sesión",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = vm.usuario,
            onValueChange = { vm.onUsuarioChange(it) },
            label = { Text("RUT o correo corporativo") },
            singleLine = true,
            colors = coloresCampoLogin(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = vm.contrasena,
            onValueChange = { vm.onContrasenaChange(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = coloresCampoLogin(),
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vm.error!!,
                color = RosaError
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuario = vm.iniciarSesion(cuentas)
                if (usuario != null) {
                    onLoginExitoso(usuario)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = AzulLogin
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Ingresar")
        }

        TextButton(
            onClick = onCrearCuenta,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta", color = Color.White)
        }
    }
}