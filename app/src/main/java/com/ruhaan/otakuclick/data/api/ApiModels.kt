package com.ruhaan.otakuclick.data.api

import com.google.gson.annotations.SerializedName

data class AnimeResponse(
    val data: List<Anime>
)

data class Anime(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages,
    val synopsis: String?,
    val score: Double?,
    val episodes: Int?,
    val status: String
)

data class AnimeImages(
    val jpg: ImageUrl
)

data class ImageUrl(
    @SerializedName("image_url")
    val imageUrl: String
)


data class DetailedAnimeResponse(
    val data: DetailedAnime,
)

data class DetailedAnime(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages,
    val synopsis: String?,
    val score: Double?,
    val episodes: Int?,
    val status: String,
    val year: Int?,
    val season: String?,
    val genres: List<Genre>,
    val studios: List<Studio>,
    val characters: List<Character>?,
    val staff: List<StaffMember>?,
    val relations: List<Relation>?,
    val recommendations: List<Recommendation>?
)

data class Genre(
    val name: String
)

data class Studio(
    val name: String
)

data class Character(
    val character: CharacterInfo,
    @SerializedName("voice_actors")
    val voiceActors: List<VoiceActor>?
)

data class CharacterInfo(
    val name: String,
    val images: CharacterImages
)

data class CharacterImages(
    val jpg: ImageUrl
)

data class VoiceActor(
    val person: PersonInfo,
    val language: String
)

data class PersonInfo(
    val name: String,
    val images: CharacterImages?
)

data class StaffMember(
    val person: PersonInfo,
    val positions: List<String>
)

data class Recommendation(
    val entry: RecommendationEntry
)

data class RecommendationEntry(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages
)

data class CharactersResponse(
    val data: List<Character>
)

data class StaffResponse(
    val data: List<StaffMember>
)

data class RecommendationsResponse(
    val data: List<Recommendation>
)

data class RelationsResponse(
    val data: List<Relation>
)

data class Relation(
    val relation: String,
    val entry: List<RelatedEntry>
)

data class RelatedEntry(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String?
)

// Response for trending/top anime endpoint
data class TrendingAnimeResponse(
    val data: List<TrendingAnimeItem>
)

// Individual trending anime item from API
data class TrendingAnimeItem(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages,
    val score: Double?,
    val year: Int?,
    val episodes: Int?,
    val synopsis: String?,
    val genres: List<Genre>? = null,
    val status: String? = null
)

// Response for genres endpoint
data class GenresResponse(
    val data: List<GenreApiItem>
)

// Individual genre from API
data class GenreApiItem(
    @SerializedName("mal_id")
    val malId: Int,
    val name: String,
    val url: String?,
    val count: Int? = null              // Number of anime in this genre (if available)
)

// Response for anime by genre
data class GenreAnimeResponse(
    val data: List<TrendingAnimeItem>   // Reuse existing trending anime structure
)

// Response for schedule endpoint
data class ScheduleResponse(
    val data: List<ScheduleAnimeItem>
)

// Individual anime item from schedule API
data class ScheduleAnimeItem(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    val images: AnimeImages,
    val score: Double?,
    val episodes: Int?,
    val synopsis: String?,
    val status: String?,
    val season: String?,
    val year: Int?,
    val broadcast: BroadcastInfo?,           // When it airs
    @SerializedName("aired")
    val airedInfo: AiredInfo?               // Air date info
)

// Broadcast timing information
data class BroadcastInfo(
    val day: String?,                       // "Monday", "Tuesday", etc.
    val time: String?,                      // "23:00" format
    val timezone: String?,                  // "JST"
    val string: String?                     // Full broadcast string
)

// Aired date information
data class AiredInfo(
    val from: String?,                      // Start date
    val to: String?,                        // End date (if finished)
    val string: String?                     // Human readable date range
)


