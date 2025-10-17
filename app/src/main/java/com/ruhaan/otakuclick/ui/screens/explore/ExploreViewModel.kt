package com.ruhaan.otakuclick.ui.screens.explore

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.models.TrendingAnimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// ViewModel for Explore screen - handles trending anime data
class ExploreViewModel : ViewModel() {


    // Trending anime list state (unchanged)
    var trendingAnime by mutableStateOf<List<TrendingAnimeItem>>(emptyList())
        private set

    // Loading and error states (unchanged)
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadTrendingAnime()    // Load trending anime when ViewModel is created
    }

    // Load trending anime from API (OPTIMIZED - uses repository with IO threading)
    fun loadTrendingAnime() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Repository call now guaranteed on IO thread
                val response = withContext(Dispatchers.IO){
                    ApiClient.jikanApi.getCurrentSeasonAnime(limit = 20)
                }

                if (response.isSuccessful) {
                    val apiAnime = response.body()?.data ?: emptyList()

                    // Transform API data to UI models (this runs on Main thread for UI updates)
                    trendingAnime = apiAnime.map { anime ->
                        TrendingAnimeItem(
                            id = anime.malId,
                            title = anime.title,
                            imageUrl = anime.images.jpg.imageUrl,
                            rating = anime.score,
                            year = anime.year?.toString(),
                            status = anime.status
                        )
                    }
                } else {
                    // Fallback to top anime if current season fails
                    loadTopAnime()
                }
            } catch (_: Exception) {
                // Network error - try fallback
                loadTopAnime()
            }

            isLoading = false
        }
    }

    // Fallback method to load top-rated anime (OPTIMIZED - uses repository)
    private suspend fun loadTopAnime() {
        try {
            // Repository call on IO thread
            val response = withContext(Dispatchers.IO){
                ApiClient.jikanApi.getTrendingAnime(limit = 20)
            }

            if (response.isSuccessful) {
                val apiAnime = response.body()?.data ?: emptyList()

                // UI updates on Main thread
                trendingAnime = apiAnime.map { anime ->
                    TrendingAnimeItem(
                        id = anime.malId,
                        title = anime.title,
                        imageUrl = anime.images.jpg.imageUrl,
                        rating = anime.score,
                        year = anime.year?.toString(),
                        status = anime.status
                    )
                }
            } else {
                errorMessage = "Failed to load trending anime"
            }
        } catch (e: Exception) {
            errorMessage = "Network error: ${e.message}"
        }
    }

    // Retry loading trending anime (unchanged)
    fun retry() {
        loadTrendingAnime()
    }
//
//    // Clear error message (unchanged)
//    fun clearError() {
//        errorMessage = null
//    }
}


//
//// ViewModel for Explore screen - handles trending anime data
//class ExploreViewModel : ViewModel() {
//
//    // Trending anime list state
//    var trendingAnime by mutableStateOf<List<TrendingAnimeItem>>(emptyList())
//        private set
//
//    // Loading and error states
//    var isLoading by mutableStateOf(false)
//        private set
//
//    var errorMessage by mutableStateOf<String?>(null)
//        private set
//
//    init {
//        loadTrendingAnime()    // Load trending anime when ViewModel is created
//    }
//
//    // Load trending anime from API
//    fun loadTrendingAnime() {
//        isLoading = true
//        errorMessage = null
//
//        viewModelScope.launch {
//            try {
//                // Try to get current season anime first (more relevant)
//                val response = ApiClient.jikanApi.getCurrentSeasonAnime(limit = 20)
//
//                if (response.isSuccessful) {
//                    val apiAnime = response.body()?.data ?: emptyList()
//
//                    // Transform API data to UI models
//                    trendingAnime = apiAnime.map { anime ->
//                        TrendingAnimeItem(
//                            id = anime.malId,
//                            title = anime.title,
//                            imageUrl = anime.images.jpg.imageUrl,
//                            rating = anime.score,
//                            year = anime.year?.toString(),
//                            status = anime.status
//                        )
//                    }
//                } else {
//                    // Fallback to top anime if current season fails
//                    loadTopAnime()
//                }
//            } catch (_: Exception) {
//                // Network error - try fallback
//                loadTopAnime()
//            }
//
//            isLoading = false
//        }
//    }
//
//    // Fallback method to load top-rated anime
//    private suspend fun loadTopAnime() {
//        try {
//            val response = ApiClient.jikanApi.getTrendingAnime(limit = 20)
//
//            if (response.isSuccessful) {
//                val apiAnime = response.body()?.data ?: emptyList()
//
//                trendingAnime = apiAnime.map { anime ->
//                    TrendingAnimeItem(
//                        id = anime.malId,
//                        title = anime.title,
//                        imageUrl = anime.images.jpg.imageUrl,
//                        rating = anime.score,
//                        year = anime.year?.toString(),
//                        status = anime.status
//                    )
//                }
//            } else {
//                errorMessage = "Failed to load trending anime"
//            }
//        } catch (e: Exception) {
//            errorMessage = "Network error: ${e.message}"
//        }
//    }
//
//    // Retry loading trending anime
//    fun retry() {
//        loadTrendingAnime()
//    }
//
//    // Clear error message
//    fun clearError() {
//        errorMessage = null
//    }
//}
