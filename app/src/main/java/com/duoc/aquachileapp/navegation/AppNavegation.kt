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
import com.duoc.aquachileapp.viewmodel.CuentasViewModel
import com.duoc.aquachileapp.viewmodel.HistorialViewModel
import com.duoc.aquachileapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    vm: HomeViewModel = viewModel(),
    historial: HistorialViewModel = viewModel(),
    cuentas: CuentasViewModel = viewModel()
) {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val mostrarMenu = rutaActual != null && rutaActual != "login" && rutaActual != "registro"

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

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

                    AppSection.entries.filter {
                        it != AppSection.APROBACIONES || vm.usuario?.esSupervisor == true
                    }.forEach { item ->

                        NavigationDrawerItem(
                            label = {
                                Text(item.seccion)
                            },

                            selected = when (item) {
                                AppSection.PRECHEQUEO ->
                                    rutaActual == "prechequeo"

                                AppSection.HISTORIAL ->
                                    rutaActual == "historial"

                                AppSection.APROBACIONES ->
                                    rutaActual == "aprobaciones"

                                AppSection.RESUMEN ->
                                    rutaActual == "resumen"

                                AppSection.SESION -> false
                            },

                            onClick = {

                                vm.seleccionar(item)

                                scope.launch {

                                    drawerState.close()

                                    when (item) {

                                        AppSection.PRECHEQUEO -> {
                                            navController.navigate("prechequeo")
                                        }

                                        AppSection.HISTORIAL -> {
                                            navController.navigate("historial")
                                        }

                                        AppSection.APROBACIONES -> {
                                            navController.navigate("aprobaciones")
                                        }

                                        AppSection.RESUMEN -> {
                                            navController.navigate("resumen")
                                        }

                                        AppSection.SESION -> {
                                            vm.cerrarSesion()
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

            bottomBar = {

                if (mostrarMenu) {

                    NavigationBar {

                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Inicio",
                                    modifier = Modifier.size(20.dp)
                                )
                            },

                            label = {
                                Text("Inicio")
                            },

                            selected = rutaActual == "home",

                            onClick = {
                                navController.navigate("home") {
                                    popUpTo("home"){
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )

                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Checklist",
                                    modifier = Modifier.size(20.dp)
                                )
                            },

                            label = {
                                Text("Checklist")
                            },

                            selected = rutaActual == "checklist" || rutaActual == "prechequeo",

                            onClick = {
                                navController.navigate("prechequeo") {
                                    launchSingleTop = true
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            }
                        )

                        NavigationBarItem(

                            icon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    modifier = Modifier.size(20.dp)
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

            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = if (mostrarMenu) Modifier.padding(padding) else Modifier
            ) {

                composable("home") {
                    HomeScreen(
                        navController = navController,
                        nombreUsuario = vm.usuario?.nombre ?: "usuario",
                        esSupervisor = vm.usuario?.esSupervisor == true,
                        historial = historial,
                        cuentas = cuentas
                    )
                }

                composable("prechequeo") {
                    PreChequeoScreen(
                        vm = historial,
                        nombreSupervisor = vm.usuario?.nombre ?: "Sin supervisor",
                        onContinuar = {
                            navController.navigate("checklist")
                        }
                    )
                }

                composable("checklist") {
                    ChecklistScreen(
                        onGuardar = { items, observaciones ->
                            historial.guardar(
                                items = items,
                                supervisor = vm.usuario?.nombre ?: "Sin supervisor",
                                observaciones = observaciones
                            )
                            navController.navigate("historial") {
                                popUpTo("home")
                            }
                        }
                    )
                }

                composable("historial") {
                    HistorialScreen(
                        vm = historial
                    )
                }

                composable("aprobaciones") {
                    if (vm.usuario?.esSupervisor == true) {
                        AprobacionesScreen(cuentas = cuentas)
                    } else {
                        Text("Solo el supervisor puede aprobar cuentas")
                    }
                }

                composable("resumen") {
                    Text("Pantalla de resumen")
                }

                composable("perfil"){
                    Text("Pantalla de perfil")
                }

                composable("registro") {
                    RegistroScreen(
                        cuentas = cuentas,
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("login") {
                    LoginScreen(
                        cuentas = cuentas,
                        onCrearCuenta = {
                            navController.navigate("registro")
                        },
                        onLoginExitoso = { usuario ->
                            vm.iniciarSesion(usuario)
                            navController.navigate("home") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}