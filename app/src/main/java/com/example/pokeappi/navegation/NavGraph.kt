package com.example.pokeappi.navegation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokeappi.Components.PokeBottomBar
import com.example.pokeappi.screens.LoginView
import com.example.pokeappi.screens.MainScreen
import com.example.pokeappi.screens.ProfileScreen
import com.example.pokeappi.screens.RegionScreen
import com.example.pokeappi.screens.RegisterView
import com.example.pokeappi.screens.TeamScreen
import com.example.pokeappi.viewModel.PokemonViewModel
import com.example.pokeappi.viewModel.ProfileViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: PokemonViewModel = viewModel()
    val context = LocalContext.current

    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authenticatedRoutes = listOf("main", "equipo", "regiones", "perfil")

    Scaffold(
        bottomBar = {
            if (currentRoute in authenticatedRoutes) {
                PokeBottomBar(
                    currentRoute = currentRoute,
                    onNavItemClick = { route ->
                        val destinationRoute = when(route) {
                            "pokedex" -> "main"
                            "equipo" -> "equipo"
                            "regiones" -> "regiones"
                            "perfil" -> "perfil"
                            else -> "main"
                        }

                        navController.navigate(destinationRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            // Vista Login
            composable("login") {
                LoginView(
                    onLoginSuccess = {
                        viewModel.listenToUserTeam()
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onCreateAccountClick = {
                        navController.navigate("register")
                    }
                )
            }

            // Vista Registro
            composable("register") {
                RegisterView(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Vista Principal (Pokedex)
            composable("main") {
                MainScreen(
                    scaffoldPadding = paddingValues,
                    viewModel = viewModel
                )
            }

            // Vista del Equipo
            composable("equipo") {
                TeamScreen(
                    viewModel = viewModel,
                    onPokemonClick = { pokemonName ->
                        viewModel.selectPokemon(pokemonName)
                    }
                )
            }

            // Vista de Regiones
            composable("regiones") {
                RegionScreen(

                )
            }

            // Vista de Perfil
            composable("perfil") {
                val profileViewModel: ProfileViewModel = viewModel()

                ProfileScreen(
                    modifier = Modifier.padding(paddingValues),
                    profileViewModel = profileViewModel,
                    pokemonViewModel = viewModel,
                    onNavigateToTeam = { navController.navigate("equipo") },
                    onNavigateToRegions = { navController.navigate("regiones") },
                    onLogoutSuccess = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}