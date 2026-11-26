package com.duoc.aquachileapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duoc.aquachileapp.ui.screens.ValidacionScreen
import com.duoc.aquachileapp.viewmodel.ValidacionViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValidacionScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun formularioAlertaYBloqueoCritico() {
        val vm = ValidacionViewModel()
        compose.setContent { MaterialTheme { ValidacionScreen(vm) } }
        compose.onNodeWithText("Edad (años)").performScrollTo().performTextReplacement("35")
        compose.onNodeWithText("Edad (años)").assertTextContains("35")
        compose.onNodeWithText("Validación preventiva local").performScrollTo().assertIsNotEnabled()
        // El primer chip No corresponde a Equipamiento del buzo.
        compose.onAllNodesWithText("No", useUnmergedTree = false)[0]
            .performScrollTo().performClick()
        compose.onNodeWithText("CIERRE Y APROBACIÓN BLOQUEADOS")
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Ítem crítico en 'No cumple': Equipamiento del buzo", substring = true)
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Aprobación Bloqueada por Seguridad")
            .performScrollTo().assertIsNotEnabled()
        compose.onAllNodesWithText("OK", useUnmergedTree = false)[0]
            .performScrollTo().performClick()
        compose.onNodeWithText("Validación preventiva local")
            .performScrollTo().assertIsNotEnabled()
    }
}
