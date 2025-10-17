package com.ruhaan.otakuclick.ui.screens.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.api.ApiClient
import kotlinx.coroutines.launch
import com.ruhaan.otakuclick.data.models.TrendingAnimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ViewModel for individual genre detail screens
class GenreDetailViewModel : ViewModel() {



    // Anime list for specific genre
    var animeList by mutableStateOf<List<TrendingAnimeItem>>(emptyList())
        private set

    // Loading and error states
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ADD: Remember the last genre ID and cache results
    private var currentGenreId: Int? = null
    private var cachedResults by mutableStateOf<List<TrendingAnimeItem>>(emptyList())


    // Load anime for specific genre
    fun loadGenreAnime(genreId: Int) {

        // If same genre and we have cached results, don't reload
        if (currentGenreId == genreId && cachedResults.isNotEmpty() && !isLoading) {
            animeList = cachedResults
            return
        }

        currentGenreId = genreId
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
//                val response = ApiClient.jikanApi.getAnimeByGenre(
//                    genreId = genreId,
//                    limit = 25
//                )

                val animeResponse = withContext(Dispatchers.IO){
                    ApiClient.jikanApi.getAnimeByGenre(
                        genreId = genreId,
                        limit = 15,           // Smaller limit for faster response
                    )
                }


                if (animeResponse.isSuccessful) {
                    val apiAnime = animeResponse.body()?.data ?: emptyList()

                    if (apiAnime.isNotEmpty()){
                        // Transform API data to UI models
                        val newResults = apiAnime.map { anime ->
                            TrendingAnimeItem(
                                id = anime.malId,
                                title = anime.title,
                                imageUrl = anime.images.jpg.imageUrl,
                                rating = anime.score,
                                year = anime.year?.toString(),
                                status = anime.status
                            )
                        }
                        animeList = newResults
                        cachedResults = newResults // Cache successful results
                        errorMessage = null
                    } else {
                        errorMessage = "No anime found for this genre"
                    }

                } else {
                    // If we have cached results, show them with warning
                    if (cachedResults.isNotEmpty()) {
                        animeList = cachedResults
                        errorMessage = "Showing cached results (API error: ${animeResponse.code()})"
                    } else {
                        errorMessage = "Failed to load genre anime (${animeResponse.code()})"
                    }
                }
            }
            catch (e: Exception) {
                // If we have cached results, show them
                if (cachedResults.isNotEmpty()) {
                    animeList = cachedResults
                    errorMessage = "Showing cached results (Network error)"
                } else {
                    errorMessage = "Network error: ${e.message}"
                }
            }

            isLoading = false
        }
    }

    // ADD: Retry function
    fun retry() {
        currentGenreId?.let { genreId ->
            loadGenreAnime(genreId)
        }
    }

    // ADD: Clear error function
    fun clearError() {
        errorMessage = null
    }

    // ADD: Force refresh (ignores cache)
    fun forceRefresh() {
        cachedResults = emptyList()
        currentGenreId?.let { genreId ->
            loadGenreAnime(genreId)
        }
    }

}