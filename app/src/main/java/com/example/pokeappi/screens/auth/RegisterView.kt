package com.example.pokeappi.screens

// Pantalla de Registro.
// Diseño: header rojo redondeado con "¡ÚNETE A LA AVENTURA!",
// tarjeta blanca con 3 campos, botón CREAR CUENTA y link a Login.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokeappi.Components.RegisterTopBar
import com.example.pokeappi.viewModel.RegisterViewModel
import com.example.pokeappi.R
import com.example.pokeappi.ui.theme.PokemonSolidFamily

// ─────────────────────────────────────────
// Paleta de colores (consistente con el proyecto)
// ─────────────────────────────────────────
private val PokeRed        = Color(0xFFE3350D)
private val PokeBackground = Color(0xFFD9D9D9)
private val FieldBg        = Color(0xFFEAEAEA)
private val CardBg         = Color(0xFFF2F2F2)
private val TextPrimary    = Color(0xFF1A1A1A)
private val TextSecondary  = Color(0xFF888888)
private val IconTint       = Color(0xFF444444)
private val ErrorColor     = Color(0xFFFF1744)

// ─────────────────────────────────────────
// Pantalla de Registro
// ─────────────────────────────────────────
@Composable
fun RegisterView(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {},
    onRegisterClick: (username: String, password: String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    var trainerName       by remember { mutableStateOf("") }
    var password          by remember { mutableStateOf("") }
    var confirmPassword   by remember { mutableStateOf("") }
    var localError        by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Scaffold(
        topBar = {
            RegisterTopBar()
        },
        bottomBar = {
            RegisterBottomBar(onNavItemClick)
        },
        containerColor = PokeBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokemonfondo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                // ── Título de sección ──
                Text(
                    text = "REGISTRO",
                    fontSize = 22.sp,
                    fontFamily = PokemonSolidFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Tarjeta blanca con los campos ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .background(color = CardBg, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Campo: Nombre de Entrenador
                    RegisterTextField(
                        value = trainerName,
                        onValueChange = { trainerName = it },
                        placeholder = "Nombre de Entrenador",
                        leadingIcon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text,
                        isPassword = false
                    )

                    // Campo: Contraseña
                    RegisterTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Contraseña",
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    // Campo: Confirmar Contraseña
                    RegisterTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirmar Contraseña",
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                val displayError = localError ?: errorMessage
                if (displayError != null) {
                    Text(
                        text = displayError,
                        color = ErrorColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                // ── Botón CREAR CUENTA ──
                Button(
                    onClick = {
                        // Valida que las contraseñas coincidan antes de ir a Firebase
                        if (password != confirmPassword) {
                            localError = "Las contraseñas no coinciden"
                        } else {
                            viewModel.registerUser(trainerName, password, onRegisterSuccess)
                        }
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
                            text = "CREAR CUENTA",
                            fontSize = 16.sp,
                            fontFamily = PokemonSolidFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.pokeball),
                            contentDescription = "Pokéball",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Link: ¿Ya tienes cuenta? Iniciar sesión ──
                val linkText = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextPrimary, fontSize = 14.sp)) {
                        append("¿Ya tienes cuenta? ")
                    }
                    withStyle(
                        SpanStyle(
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Iniciar sesión")
                    }
                }
                Text(
                    text = linkText,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ─────────────────────────────────────────
// Componente: campo de texto reutilizable
// ─────────────────────────────────────────
@Composable
fun RegisterTextField(
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
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor             = PokeRed,
            focusedTextColor        = TextPrimary,
            unfocusedTextColor      = TextPrimary
        )
    )
}

// ─────────────────────────────────────────
// Componente: Barra de navegación inferior
// ─────────────────────────────────────────
@Composable
fun RegisterBottomBar(onNavItemClick: (String) -> Unit = {}) {
    BottomAppBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RegisterNavItem(
                icon = Icons.Default.List,
                label = "POKÉDEX",
                isSelected = true,
                onClick = { onNavItemClick("pokedex") }
            )
            RegisterNavItem(
                icon = Icons.Default.FavoriteBorder,
                label = "EQUIPO",
                isSelected = false,
                onClick = { onNavItemClick("equipo") }
            )
            RegisterNavItem(
                icon = Icons.Default.Place,
                label = "REGIONES",
                isSelected = false,
                onClick = { onNavItemClick("regiones") }
            )
            RegisterNavItem(
                icon = Icons.Default.Person,
                label = "PERFIL",
                isSelected = false,
                onClick = { onNavItemClick("perfil") }
            )
        }
    }
}

// ─────────────────────────────────────────
// Componente: ítem del menú inferior
// ─────────────────────────────────────────
@Composable
fun RegisterNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PokeRed else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PokeRed else Color.Gray,
            letterSpacing = 0.5.sp
        )
    }
}