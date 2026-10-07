package com.duoc.aquachileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.aquachileapp.navegation.AppNavigation
import com.duoc.aquachileapp.ui.screens.ChecklistScreen
import com.duoc.aquachileapp.ui.screens.HomeScreen
import com.duoc.aquachileapp.ui.theme.AquaChileAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaChileAppTheme {
                ChecklistScreen()
                AppNavigation()
            }
        }
    }
}