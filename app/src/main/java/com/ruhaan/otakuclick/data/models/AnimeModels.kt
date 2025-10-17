package com.ruhaan.otakuclick.data.models

import com.google.gson.annotations.SerializedName

// Basic anime item for search results and grid displays
data class AnimeItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val score: Double,
    val episodes: Int,
    val synopsis: String
)
// API response wrapper for search results
data class AnimeResponse(
    val data: List<AnimeSearchResult>     // API returns array of search results
)

data class AnimeSearchResult(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages,
    val synopsis: String?,
    val score: Double?,
    val episodes: Int?
)

// Image structure from API
data class AnimeImages(
    val jpg: ImageUrl
)

// Image URL structure
data class ImageUrl(
    @SerializedName("image_url")
    val imageUrl: String
)

data class AnimeDetail(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String,
    val score: Double,
    val episodes: Int,
    val status: String,
    val year: Int?,
    val season: String?,
    val genres: List<String>,
    val studios: List<String>
)

data class CharacterItem(
    val name: String,
    val imageUrl: String,
    val voiceActor: String?,
    val language: String?
)

data class StaffItem(
    val name: String,
    val imageUrl: String?,
    val positions: List<String>
)

data class RelatedAnime(
    val id: Int,
    val title: String,
    val relation: String,
    val type: String
)

data class RecommendedAnime(
    val id: Int,
    val title: String,
    val imageUrl: String
)

// UI model for trending anime display
data class TrendingAnimeItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double?,
    val year: String?,
    val status: String?
)
