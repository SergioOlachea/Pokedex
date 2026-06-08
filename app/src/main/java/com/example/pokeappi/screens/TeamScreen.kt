package com.example.pokeappi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.painterResource
import com.example.pokeappi.R
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokeappi.Components.MessageCard
import com.example.pokeappi.Components.PokemonCard
import com.example.pokeappi.Components.PokemonDetails
import com.example.pokeappi.Components.PokemonTopBar
import com.example.pokeappi.ui.theme.PokemonHollowFamily
import com.example.pokeappi.viewModel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Nombres de los integrantes en Firebase (Set<String>)
    val teamByNames by viewModel.pokemonTeam.collectAsState()

    // Lista completa de Pokémon de tu API (es un MutableState)
    val allPokemons = viewModel.filteredPokemon.value

    // Filtra la lista global conservando solo los que están en el equipo
    val teamList = allPokemons.filter { teamByNames.contains(it.name) }
    val selectedDetail by viewModel.selectedPokemonDetail
    val showBottomSheet by viewModel.showDetailBottomSheet
    val sheetState = rememberModalBottomSheetState()
    val speciesInfo by viewModel.speciesInfo
    val searchText = viewModel.searchText.value

    Scaffold(
        topBar = {
            PokemonTopBar(
                searchText = searchText,
                onSearchValueChange = { newText ->
                    viewModel.onSearchTextChange(newText)
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
    Column(
        modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        Text(
            text = "Mi Equipo Pokémon (${teamList.size}/6)",
            fontFamily = PokemonHollowFamily,
            fontSize = 30.sp,
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
// Mensaje cuando el equipo esta vacio o no coincide con la busqueda
        if (teamList.isEmpty()) {
            if (searchText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MessageCard(
                        title = "Sin Coincidencias",
                        message = "Ningún Pokémon en tu equipo coincide con \"$searchText\".",
                        icon = Icons.Default.Search,
                        iconSize = 60.dp,
                        imageSize = 30.dp,
                        cardPadding = 24.dp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MessageCard(
                        title = "¡Equipo Vacío!",
                        message = "Tu equipo está vacío. ¡Ve a la pantalla principal y selecciona hasta 6 Pokémon!",
                        imagePainter = painterResource(id = R.drawable.pokeball),
                        iconSize = 100.dp,
                        imageSize = 56.dp,
                        cardPadding = 32.dp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(teamList) { pokemon ->
                    PokemonCard(
                        pokemon = null,
                        name = pokemon.name,
                        url = pokemon.url,
                        type = pokemon.type,
                        isInTeam = true,
                        onClick = { onPokemonClick(pokemon.name) },
                        onTeamToggle = { viewModel.toggleTeamMember(pokemon.name) }
                    )
                }
            }
        }
        }
    }
// Ventana emergente de detalles (BottomSheet)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeDetail() },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            val descriptionEs = speciesInfo?.flavorTextEntries?.find { it.language.name == "es" }?.flavorText ?: "Sin descripción"
            val types = selectedDetail?.types?.map { it.type.name} ?: emptyList()

            // Contenido del detalle del Pokemon
            PokemonDetails(
                detail = selectedDetail,
                description = descriptionEs,
                types = types,
                onClose = { viewModel.closeDetail() }
            )
        }
    }
}
