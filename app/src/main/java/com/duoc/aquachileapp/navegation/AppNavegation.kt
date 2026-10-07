package com.duoc.aquachileapp.navegation


import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.duoc.aquachileapp.model.AppSection
import com.duoc.aquachileapp.ui.screens.*
import com.duoc.aquachileapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    vm: HomeViewModel = viewModel()
) {

    // Controlador de navegación
    val navController = rememberNavController()

    // Detecta la pantalla actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // No mostrar menú en Login
    val mostrarMenu = rutaActual != "login"

    // Estado del menú hamburguesa
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    // MENÚ LATERAL
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarMenu,

        drawerContent = {

            if (mostrarMenu) {
                ModalDrawerSheet {

                    Text(
                        text = "AquaCheck Buceo",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    HorizontalDivider()

                    AppSection.entries.forEach { item ->

                        NavigationDrawerItem(
                            label = {
                                Text(item.seccion)
                            },

                            selected = when (item) {
                                AppSection.PRECHEQUEO ->
                                    rutaActual == "prechequeo"

                                AppSection.RESUMEN ->
                                    rutaActual == "resumen"

                                AppSection.SESION -> false
                            },

                            onClick = {

                                // Actualiza el ViewModel
                                vm.seleccionar(item)

                                scope.launch {

                                    // Cierra el menú
                                    drawerState.close()

                                    // Navega según la opción
                                    when (item) {

                                        AppSection.PRECHEQUEO -> {
                                            navController.navigate("prechequeo")
                                        }

                                        AppSection.RESUMEN -> {
                                            navController.navigate("resumen")
                                        }

                                        AppSection.SESION -> {
                                            navController.navigate("login") {
                                                popUpTo("home") {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {

        Scaffold(

            // BARRA SUPERIOR COMPARTIDA
            topBar = {

                if (mostrarMenu) {

                    TopAppBar(
                        title = {
                            Text("AquaCheck Buceo")
                        },

                        navigationIcon = {

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                }
            },
            // BARRA INFERIOR
            bottomBar = {

                if (mostrarMenu) {

                    NavigationBar {

                        // INICIO
                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Inicio"
                                )
                            },

                            label = {
                                Text("Inicio")
                            },

                            selected = rutaActual == "home",

                            onClick = {
                                navController.navigate("home") {
                                    //Evita acumular las pantallas anteriores
                                    popUpTo("home"){
                                        inclusive = false
                                    }
                                    //evita abrir varias veces home
                                    launchSingleTop = true

                                }

                            }
                        )


                        // CHECKLIST
                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Checklist"
                                )
                            },

                            label = {
                                Text("Checklist")
                            },

                            selected = rutaActual == "checklist",

                            onClick = {
                                navController.navigate("checklist") {
                                    launchSingleTop = true
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            }
                        )


                        // PERFIL
                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil"
                                )
                            },

                            label = {
                                Text("Perfil")
                            },

                            selected = rutaActual == "perfil",

                            onClick = {
                                navController.navigate("perfil") {
                                    launchSingleTop = true
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            }
                        )

                    }
                }
            }

        ) { padding ->

            // NAVEGACIÓN ENTRE PANTALLAS
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {

                // HOME
                composable("home") {
                    HomeScreen(
                        navController = navController
                    )
                }

                // PRE-CHEQUEO
                composable("prechequeo") {
                    PreChequeoScreen(
                        onPreChequeo = {
                            navController.navigate("checklist")
                        }
                    )
                }

                // CHECKLIST
                composable("checklist") {
                    ChecklistScreen()
                }

                // RESUMEN
                composable("resumen") {
                    Text("Pantalla de resumen")
                }

                //PERFIL
                composable("perfil"){
                    Text("Pantalla de perfil")
                }

                // LOGIN
                composable("login") {
                    Text("Pantalla de Login")
                }
            }
        }
    }
}
