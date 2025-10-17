package com.ruhaan.otakuclick.ui.screens.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.models.Genre
import com.ruhaan.otakuclick.ui.utils.ColorUtils
import kotlinx.coroutines.launch


// ViewModel for Categories screen - manages genre data
class CategoriesViewModel : ViewModel() {



    // List of all available genres
    var genreList by mutableStateOf<List<Genre>>(emptyList())
        private set

    // Loading and error states
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadGenres()    // Load genres when ViewModel is created
    }

    // Load all available anime genres from API
    fun loadGenres() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
//                val response = ApiClient.jikanApi.getAnimeGenres()
                val genresResponse = ApiClient.jikanApi.getAnimeGenres()


                if (genresResponse.isSuccessful) {
                    val apiGenres = genresResponse.body()?.data ?: emptyList()

                    // Transform API data to UI models with colors
                    genreList = apiGenres
                        .filter { it.name.isNotBlank() }        // Filter out empty names
                        .take(24)                               // Limit to 24 genres for performance
                        .map { apiGenre ->
                            Genre(
                                id = apiGenre.malId,
                                name = apiGenre.name,
                                color = ColorUtils.getColorForGenre(apiGenre.name),
                                animeCount = apiGenre.count
                            )
                        }
                        .sortedBy { it.name }                   // Alphabetical order

                } else {
                    errorMessage = "Failed to load genres (${genresResponse.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            }

            isLoading = false
        }
    }

    // Retry loading genres
    fun retry() {
        loadGenres()
    }

//    // Clear error message
//    fun clearError() {
//        errorMessage = null
//    }
}