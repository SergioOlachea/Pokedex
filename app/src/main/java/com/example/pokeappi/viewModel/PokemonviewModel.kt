package com.example.pokeappi.viewModel

//gestionar los datos de los Pokémon y controlar qué se muestra en la pantalla.

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeappi.api.RetrofitInstance
import com.example.pokeappi.models.PokemonDetailResponse
import com.example.pokeappi.models.PokemonSpecies
import com.example.pokeappi.models.SimplePokemon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {

    // Lista para el estado de la UI
    var pokemonList = mutableStateOf<List<SimplePokemon>>(emptyList())

    // Lista de apoyo para guardar los originales y no perderlos al filtrar
    private var allPokemon = listOf<SimplePokemon>()

    // Lista que se actualiza con el filtro de busqueda
    var filteredPokemon = mutableStateOf<List<SimplePokemon>>(emptyList())

    // Texto de busqueda para mantenerlo entre las pantallas Main y Team
    var searchText = mutableStateOf("")

    // Estados para los detalles del pokemon
    private val _selectedPokemonDetail = mutableStateOf<PokemonDetailResponse?>(null)
    val selectedPokemonDetail: State<PokemonDetailResponse?> = _selectedPokemonDetail

    // Estado para mostrar o cerrar el BottomSheet
    private val _showDetailBottomSheet = mutableStateOf(false)
    val showDetailBottomSheet: State<Boolean> = _showDetailBottomSheet

    // Estado para la informacion de la especie
    private val _speciesInfo = mutableStateOf<PokemonSpecies?>(null)
    val speciesInfo: State<PokemonSpecies?> = _speciesInfo


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado para la UI
    private val _pokemonTeam = MutableStateFlow<Set<String>>(emptySet())
    val pokemonTeam: StateFlow<Set<String>> = _pokemonTeam.asStateFlow()

    // Estado para mensajes/errores
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // ID del usuario actual de Firebase
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        listenToUserTeam()
    }

    init {
        fetchList()
    }

    // Funcion para obtener la lista de pokemon de la API con sus tipos
    private fun fetchList() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPokemonList(151)
                val detailedPokemon = response.results.map { simple ->
                    async {
                        try {
                            val detail = RetrofitInstance.api.getPokemonDetails(simple.name)
                            simple.copy(type = detail.types.firstOrNull()?.type?.name)
                        } catch (e: Exception) {
                            simple
                        }
                    }
                }.awaitAll()

                allPokemon = detailedPokemon
                pokemonList.value = detailedPokemon
                filteredPokemon.value = detailedPokemon
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Funcion para cerrar el detalle del pokemon
    fun closeDetail() {
        _showDetailBottomSheet.value = false
        _selectedPokemonDetail.value = null
    }

    // Funcion para seleccionar un pokemon y cargar sus datos
    fun selectPokemon(name: String) {
        viewModelScope.launch {
            _showDetailBottomSheet.value = true
            _selectedPokemonDetail.value = null
            _speciesInfo.value = null

            try {
                val detailDeferred = async { RetrofitInstance.api.getPokemonDetails(name) }
                val speciesDeferred = async { RetrofitInstance.api.getPokemonSpecies(name) }

                _selectedPokemonDetail.value = detailDeferred.await()
                _speciesInfo.value = speciesDeferred.await()

            } catch (e: Exception) {
                _showDetailBottomSheet.value = false
                e.printStackTrace()
            }
        }
    }

    // Funcion para el filtro de busqueda por nombre o numero
    fun onSearchTextChange(query: String) {
        searchText.value = query
        filteredPokemon.value = if (query.isEmpty()) {
            allPokemon
        } else {
            allPokemon.filter { pokemon ->
                // Obtenemos el ID desde la URL del modelo
                val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()

                // Filtramos si empieza con la letra o si es el numero exacto
                pokemon.name.startsWith(query, ignoreCase = true) || id == query
            }
        }
    }

    fun listenToUserTeam() {
        val userId = currentUserId ?: return

        db.collection("teams").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val pokemonsList = snapshot.get("pokemons") as? List<String>
                _pokemonTeam.value = pokemonsList?.toSet() ?: emptySet()
            }
    }

    fun toggleTeamMember(pokemonName: String) {
        val userId = currentUserId ?: return
        val currentTeam = _pokemonTeam.value.toMutableList()

        if (currentTeam.contains(pokemonName)) {
            currentTeam.remove(pokemonName)
        } else {
            if (currentTeam.size < 6) {
                currentTeam.add(pokemonName)
            } else {
                _errorMessage.value = "¡Tu equipo está lleno! No puedes tener más de 6 Pokémon."
                return
            }
        }

        db.collection("teams").document(userId)
            .set(mapOf("pokemons" to currentTeam))
            .addOnFailureListener {
            }
    }
}