package com.duoc.aquachileapp.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.Usuario


@Composable
fun PerfilScreen(
    usuario: Usuario?
) {

    val nombre = usuario?.nombre ?: "Usuario"


    val rol = if (usuario?.esSupervisor == true) {
        "Supervisor"
    } else {
        "Buzo"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = nombre
                    .firstOrNull()
                    ?.uppercase()
                    ?: "U",

                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = nombre,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = rol,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Información del usuario",
                    style = MaterialTheme.typography.titleMedium
                )

                DatoPerfil(
                    titulo = "Nombre",
                    valor = nombre
                )

                DatoPerfil(
                    titulo = "Rol",
                    valor = rol
                )

                DatoPerfil(
                    titulo = "Estado",
                    valor = "Activo"
                )
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
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}