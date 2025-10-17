package com.ruhaan.otakuclick.data.models

import androidx.compose.ui.graphics.Color


// UI model for genre display
data class Genre(
    val id: Int,
    val name: String,
    val color: Color,                   // Dynamic color for card background
    val animeCount: Int? = null        // Number of anime in genre (optional)
)

// Genre detail screen state
data class GenreDetail(
    val genre: Genre,
    val animeList: List<TrendingAnimeItem>,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

