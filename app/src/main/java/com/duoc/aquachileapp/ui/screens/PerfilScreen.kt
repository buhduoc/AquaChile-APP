package com.duoc.aquachileapp.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.duoc.aquachileapp.R


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.aquachileapp.model.Usuario


@Composable
fun PerfilScreen(
    usuario: Usuario?
) {

    val nombre = usuario?.nombre ?: "Usuario"
    val rol = usuario?.rol ?: "Sin rol"
    val rut = usuario?.rut ?:"Sin rut"
    val telefono = usuario?.telefono ?:"Sin telefono"
    val correo = usuario?.correo ?:"Sin correo"

    //Seleccionar foto segun usuario
    val fotoPerfil = if(usuario?.esSupervisor ==true){
        R.drawable.supervisor_demo
    } else{
        R.drawable.buzo_demo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(90.dp)
        )

        Image(
            painter =painterResource(id = fotoPerfil),

            contentDescription = "Foto de perfil",

            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape),

            contentScale = ContentScale.Crop
        )

        //Espacio entre Foto de perfil con informacion del usuario, rol etc
        Spacer(
            modifier = Modifier.height(30.dp)
        )



        // Nombre, rol, rut, telefono y correo


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


        Spacer(
            modifier = Modifier.height(35.dp)
        )



        // Informacion del usuario


        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(14.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Información del usuario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )


                    // Etiqueta "Perfil Activo"
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFBDF5E7),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                    ) {

                        Text(
                            text = "Perfil Activo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF168C79)
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                DatoPerfil(
                    titulo = "Nombre",
                    valor = nombre
                )


                SeparadorPerfil()


                DatoPerfil(
                    titulo = "Rol",
                    valor = rol
                )


                SeparadorPerfil()

                DatoPerfil(
                    titulo = "Rut",
                    valor = rut
                )
                SeparadorPerfil()

                DatoPerfil(
                    titulo = "Telefono",
                    valor = telefono
                )
                SeparadorPerfil()

                DatoPerfil(
                    titulo = "Correo",
                    valor = correo
                )



                // Estado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF707784)
                    )


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "●",
                            color = Color(0xFF65BCAA)
                        )

                        Spacer(
                            modifier = Modifier.size(5.dp)
                        )

                        Text(
                            text = "Activo",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF369A88)
                        )
                    }
                }
            }
        }

    }
}



// Fila de datos


@Composable
fun DatoPerfil(
    titulo: String,
    valor: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
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



// Separador


@Composable
fun SeparadorPerfil() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Color(0xFFF0F1F4)
            )
    )
}



// Opciones inferiores


@Composable
fun OpcionPerfil(
    titulo: String,
    subtitulo: String?,
    icono: @Composable () -> Unit,
    accion: @Composable () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF1F4FF),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.size(28.dp),

            contentAlignment =
                Alignment.Center
        ) {

            icono()
        }


        Spacer(
            modifier = Modifier.size(8.dp)
        )


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263247)
            )


            if (subtitulo != null) {

                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF717784)
                )
            }
        }


        accion()
    }
}