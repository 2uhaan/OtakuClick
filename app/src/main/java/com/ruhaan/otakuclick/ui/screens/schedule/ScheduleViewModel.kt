package com.ruhaan.otakuclick.ui.screens.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.models.ScheduleItem
import kotlinx.coroutines.launch

// Simplified ViewModel for Schedule screen - only upcoming anime
class ScheduleViewModel : ViewModel() {


    // List of upcoming anime
    var upcomingAnime by mutableStateOf<List<ScheduleItem>>(emptyList())
        private set

    // Loading and error states
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadUpcomingAnime()    // Load upcoming anime when ViewModel is created
    }

    // Load upcoming anime releases
    fun loadUpcomingAnime() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Try upcoming endpoint first
//                var response = ApiClient.jikanApi.getUpcomingAnime(limit = 25)
                var response = ApiClient.jikanApi.getUpcomingAnime()


                if (!response.isSuccessful || response.body()?.data.isNullOrEmpty()) {
                    // Fallback to top upcoming anime
//                    response = ApiClient.jikanApi.getTopUpcomingAnime()
                    response = ApiClient.jikanApi.getTopUpcomingAnime()
                }

                if (!response.isSuccessful || response.body()?.data.isNullOrEmpty()) {
                    // Final fallback - use current season anime
                    response = ApiClient.jikanApi.getCurrentSeasonSchedule()
                }

                if (response.isSuccessful) {
                    val apiAnime = response.body()?.data ?: emptyList()

                    if (apiAnime.isNotEmpty()) {
                        // Transform to UI models
                        upcomingAnime = apiAnime.mapNotNull { anime ->
                            if (anime.title.isBlank()) return@mapNotNull null

                            ScheduleItem(
                                id = anime.malId,
                                title = anime.title,
                                imageUrl = anime.images.jpg.imageUrl,
                                episode = null,
                                airDay = anime.broadcast?.day,
                                airTime = formatAirTime(anime.broadcast?.time),
                                status = anime.status ?: "Coming Soon",
                                rating = anime.score,
                                year = anime.year,
                                isToday = false,
                                isUpcoming = true
                            )
                        }.sortedWith(
                            compareByDescending<ScheduleItem> { it.rating ?: 0.0 }
                                .thenBy { it.title }
                        )
                    } else {
                        errorMessage = "No upcoming anime data received"
                    }
                } else {
                    errorMessage = "API Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            }

            isLoading = false
        }
    }


    // Format air time from 24h to 12h format
    private fun formatAirTime(time: String?): String? {
        if (time.isNullOrBlank()) return null

        return try {
            val inputFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.ENGLISH)
            val outputFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.ENGLISH)
            val date = inputFormat.parse(time)
            outputFormat.format(date ?: return null)
        } catch (_: Exception) {
            time  // Return original if parsing fails
        }
    }

    // Retry loading upcoming anime
    fun retry() {
        loadUpcomingAnime()
    }

//    // Clear error message
//    fun clearError() {
//        errorMessage = null
//    }
}