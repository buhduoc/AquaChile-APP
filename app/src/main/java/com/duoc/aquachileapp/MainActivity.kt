package com.duoc.aquachileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.aquachileapp.navegation.AppNavigation
import com.duoc.aquachileapp.ui.theme.AquaChileAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.duoc.aquachileapp.data.Remote.SesionSegura.iniciar(applicationContext)
        coil.Coil.setImageLoader(coil.ImageLoader.Builder(this).okHttpClient(com.duoc.aquachileapp.data.Remote.RetrofitClient.cliente).build())
        enableEdgeToEdge()
        setContent {
            AquaChileAppTheme {
                AppNavigation()
            }
        }
    }
}

