package com.example.pokeappi.screens

// Pantalla de inicio de sesión.
// Diseño: header rojo redondeado, campos estilizados con íconos,
// link de recuperación, botón principal y barra de navegación inferior.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.pokeappi.Components.PokeBottomBar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokeappi.Components.LoginTopBar
import com.example.pokeappi.R
import com.example.pokeappi.ui.theme.PokemonSolidFamily
import com.example.pokeappi.viewModel.LoginViewModel

// ─────────────────────────────────────────
// Paleta de colores (consistente con MainScreen)
// ─────────────────────────────────────────
private val PokeRed        = Color(0xFFE3350D)
private val PokeBackground = Color.White
private val FieldBg        = Color(0xFFEAEAEA)
private val CardBg         = Color(0xFFF2F2F2)
private val TextPrimary    = Color(0xFF1A1A1A)
private val TextSecondary  = Color(0xFF666666)
private val IconTint       = Color(0xFF444444)
private val ErrorColor     = Color(0xFFFF1744)

// ─────────────────────────────────────────
// Pantalla de Login
// ─────────────────────────────────────────
@Composable
fun LoginView(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onLoginClick: (username: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    var trainerName by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Scaffold(
        topBar = {
            LoginTopBar()
        },
        bottomBar = {
            PokeBottomBar(
                currentRoute = null,
                onNavItemClick = onNavItemClick
            )
        },
        containerColor = PokeBackground
    ) { paddingValues ->
        // ── Cuerpo del formulario ──
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokemonfondo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(36.dp))

                // Título de sección
                Text(
                    text = "INICIO DE SESIÓN",
                    fontSize = 30.sp,
                    fontFamily = PokemonSolidFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .background(color = CardBg, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        LoginTextField(
                            value = trainerName,
                            onValueChange = {
                                trainerName = it
                                viewModel.clearError()
                            },
                            placeholder = "Nombre de Entrenador",
                            leadingIcon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text,
                            isPassword = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Campo: Contraseña ──
                        LoginTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                viewModel.clearError()
                            },
                            placeholder = "Contraseña",
                            leadingIcon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Password,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // ── Link: ¿Olvidaste tu código? ──
                        Text(
                            text = "¿Olvidaste tu código de entrenador?",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .clickable { onForgotPasswordClick() }
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = ErrorColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // ── Botón principal: INICIAR SESIÓN ──
                Button(
                    onClick = {
                        viewModel.loginUser(trainerName, password, onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PokeRed,
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = "INICIAR SESIÓN",
                            fontSize = 16.sp,
                            fontFamily = PokemonSolidFamily,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.pokeball),
                            contentDescription = "Pokéball",
                            tint = Color.White,
                            modifier = Modifier.size(66.dp)
                        )
                    }
                }
                // ── Link: Crear cuenta nueva ──
                Text(
                    text = "Crear cuenta nueva",
                    fontSize = 14.sp,
                    color = TextPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onCreateAccountClick() }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


// ─────────────────────────────────────────
// Componente reutilizable: campo de texto
// ─────────────────────────────────────────
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 15.sp,
                color = TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = IconTint,
                modifier = Modifier.size(26.dp)
            )
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PokeRed,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )
    )
}



