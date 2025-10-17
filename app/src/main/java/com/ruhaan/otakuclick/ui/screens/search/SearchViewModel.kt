package com.ruhaan.otakuclick.ui.screens.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.models.AnimeItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ViewModel for search functionality - manages search state and API calls
class SearchViewModel : ViewModel() {


    // Search results list
    var animeList by mutableStateOf<List<AnimeItem>>(emptyList())
        private set

    // Loading state
    var isLoading by mutableStateOf(false)
        private set

    // Error message state
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Main search function
    fun searchAnime(query: String) {
        if (query.isBlank()) return

        isLoading = true
        errorMessage = null
        animeList = emptyList()  // Clear previous results

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    ApiClient.jikanApi.searchAnime(query)
                }

                if (response.isSuccessful) {
                    val searchResults = response.body()?.data?.map { result ->
                        AnimeItem(
                            id = result.malId,
                            title = result.title,
                            imageUrl = result.images.jpg.imageUrl,
                            score = result.score ?: 0.0,
                            episodes = result.episodes ?: 0,
                            synopsis = result.synopsis ?: "No synopsis available"
                        )
                    } ?: emptyList()

                    animeList = searchResults
                } else {
                    errorMessage = "Search failed (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            }

            isLoading = false
        }
    }

    // Clear error message
    fun clearError() {
        errorMessage = null
        animeList = emptyList()  // Also clear results when clearing error
    }

    // Clear all search data
    fun clearSearch() {
        animeList = emptyList()
        errorMessage = null
        isLoading = false
    }

}