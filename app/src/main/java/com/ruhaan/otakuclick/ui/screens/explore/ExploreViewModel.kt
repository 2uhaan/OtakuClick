package com.ruhaan.otakuclick.ui.screens.explore

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.cache.AppDatabase
import com.ruhaan.otakuclick.data.cache.CachedAnime
import com.ruhaan.otakuclick.data.models.TrendingAnimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel for Explore screen - handles trending anime data
class ExploreViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val animeDao = database.animeDao()


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

                // 1. Try to load from cache first
                val cachedData = withContext(Dispatchers.IO) {
                    animeDao.getCachedAnime("trending")
                }

                // 2. If cache exists and is recent (less than 10 minutes), use it
                val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)
                val recentCache = cachedData.filter { it.cachedAt > tenMinutesAgo }

                if (recentCache.isNotEmpty()) {
                    // Show cached data immediately
                    trendingAnime = recentCache.map { it.toTrendingAnimeItem() }
                }

                // Repository call now guaranteed on IO thread
                val response = withContext(Dispatchers.IO){
                    ApiClient.jikanApi.getCurrentSeasonAnime(limit = 20)
                }

                if (response.isSuccessful) {
                    val apiAnime = response.body()?.data ?: emptyList()

                    // Transform API data to UI models (this runs on Main thread for UI updates)
                    val newAnimeList = apiAnime.map { anime ->
                        TrendingAnimeItem(
                            id = anime.malId,
                            title = anime.title,
                            imageUrl = anime.images.jpg.imageUrl,
                            rating = anime.score,
                            year = anime.year?.toString(),
                            status = anime.status
                        )
                    }

                    // 4. Update UI with fresh data
                    trendingAnime = newAnimeList

                    // 5. Save to cache for next time
                    withContext(Dispatchers.IO) {
                        animeDao.clearCacheType("trending") // Clear old cache
                        animeDao.insertAnime(
                            newAnimeList.map { it.toCachedAnime("trending") }
                        )
                    }


                } else if (recentCache.isEmpty()) {
                    // Fallback to top anime if current season fails
                    loadTopAnime()
                }
            } catch (_: Exception) {
                // Network error - try fallback
//                loadTopAnime()
                if (trendingAnime.isEmpty()) {
                    loadTopAnime()
                }
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

// Extension functions for easy conversion
private fun CachedAnime.toTrendingAnimeItem(): TrendingAnimeItem {
    return TrendingAnimeItem(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        rating = this.rating,
        year = this.year,
        status = this.status
    )
}

private fun TrendingAnimeItem.toCachedAnime(type: String): CachedAnime {
    return CachedAnime(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        rating = this.rating,
        year = this.year,
        status = this.status,
        cacheType = type
    )
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
