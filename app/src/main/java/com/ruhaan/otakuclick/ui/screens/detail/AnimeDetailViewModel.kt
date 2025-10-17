package com.ruhaan.otakuclick.ui.screens.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.DetailScreenState
import com.ruhaan.otakuclick.data.LoadingState
import com.ruhaan.otakuclick.data.api.ApiClient
import com.ruhaan.otakuclick.data.models.AnimeDetail
import com.ruhaan.otakuclick.data.models.CharacterItem
import com.ruhaan.otakuclick.data.models.RecommendedAnime
import com.ruhaan.otakuclick.data.models.RelatedAnime
import com.ruhaan.otakuclick.data.models.StaffItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class AnimeDetailViewModel : ViewModel() {


    var screenState by mutableStateOf(DetailScreenState())
        private set

    private var currentAnimeId: Int = 0

    fun loadAnimeDetails(animeId: Int) {
        if (animeId <= 0 || animeId == currentAnimeId) return

        currentAnimeId = animeId
        resetState()

        viewModelScope.launch {
            // Load basic details first
            loadBasicDetails(animeId)

            // Then load other sections with delay and retry
            delay(500) // Small delay to avoid overwhelming API

            launch { loadCharactersWithRetry(animeId) }
            launch { loadStaffWithRetry(animeId) }
            launch { loadRelationsWithRetry(animeId) }
            launch { loadRecommendationsWithRetry(animeId) }
        }
    }

    private fun resetState() {
        screenState = DetailScreenState()
    }

    private suspend fun loadBasicDetails(animeId: Int) {
        try {
            val response = ApiClient.jikanApi.getAnimeDetails(animeId)


            if (response.isSuccessful) {
                val anime = response.body()?.data

                if (anime != null) {
                    val animeDetail = AnimeDetail(
                        id = anime.malId,
                        title = anime.title,
                        imageUrl = anime.images.jpg.imageUrl,
                        synopsis = anime.synopsis ?: "No synopsis available",
                        score = anime.score ?: 0.0,
                        episodes = anime.episodes ?: 0,
                        status = anime.status,
                        year = anime.year,
                        season = anime.season,
                        genres = anime.genres.map { it.name },
                        studios = anime.studios.map { it.name }
                    )

                    screenState = screenState.copy(
                        animeDetail = LoadingState.Success(animeDetail)
                    )
                } else {
                    screenState = screenState.copy(
                        animeDetail = LoadingState.Error("No anime data received")
                    )
                }
            } else {
                screenState = screenState.copy(
                    animeDetail = LoadingState.Error("Failed to load anime details")
                )
            }
        } catch (e: Exception) {
            screenState = screenState.copy(
                animeDetail = LoadingState.Error("Network error: ${e.message}")
            )
        }
    }

    private suspend fun loadCharactersWithRetry(animeId: Int, retryCount: Int = 0) {
        try {
            delay(retryCount * 1000L) // Increasing delay for retries
            val charactersResponse = ApiClient.jikanApi.getAnimeCharacters(animeId)

            if (charactersResponse.isSuccessful) {
                val characters = charactersResponse.body()?.data?.take(12)?.map { character ->
                    CharacterItem(
                        name = character.character.name,
                        imageUrl = character.character.images.jpg.imageUrl,
                        voiceActor = character.voiceActors?.firstOrNull()?.person?.name,
                        language = character.voiceActors?.firstOrNull()?.language
                    )
                } ?: emptyList()

                screenState = screenState.copy(
                    characters = LoadingState.Success(characters)
                )
            } else if (retryCount < 2) {
                loadCharactersWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    characters = LoadingState.Success(emptyList())
                )
            }
        } catch (_: Exception) {
            if (retryCount < 2) {
                loadCharactersWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    characters = LoadingState.Success(emptyList())
                )
            }
        }
    }

    private suspend fun loadStaffWithRetry(animeId: Int, retryCount: Int = 0) {
        try {
            delay(retryCount * 1000L)
            val staffResponse = ApiClient.jikanApi.getAnimeStaff(animeId)

            if (staffResponse.isSuccessful) {
                val staff = staffResponse.body()?.data?.take(10)?.map { staffMember ->
                    StaffItem(
                        name = staffMember.person.name,
                        imageUrl = staffMember.person.images?.jpg?.imageUrl,
                        positions = staffMember.positions
                    )
                } ?: emptyList()

                screenState = screenState.copy(
                    staff = LoadingState.Success(staff)
                )
            } else if (retryCount < 2) {
                loadStaffWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    staff = LoadingState.Success(emptyList())
                )
            }
        } catch (_: Exception) {
            if (retryCount < 2) {
                loadStaffWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    staff = LoadingState.Success(emptyList())
                )
            }
        }
    }

    private suspend fun loadRelationsWithRetry(animeId: Int, retryCount: Int = 0) {
        try {
            delay(retryCount * 1000L)
//            val response = ApiClient.jikanApi.getAnimeDetails(animeId)
            val response = ApiClient.jikanApi.getAnimeDetails(animeId)


            if (response.isSuccessful) {
                val anime = response.body()?.data
                val related = mutableListOf<RelatedAnime>()

                anime?.relations?.forEach { relation ->
                    relation.entry.forEach { entry ->
                        if (entry.type.equals("anime", ignoreCase = true)) {
                            related.add(
                                RelatedAnime(
                                    id = entry.malId,
                                    title = entry.name,
                                    relation = relation.relation,
                                    type = entry.type
                                )
                            )
                        }
                    }
                }

                screenState = screenState.copy(
                    relatedAnime = LoadingState.Success(related.take(8))
                )
            } else if (retryCount < 2) {
                loadRelationsWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    relatedAnime = LoadingState.Success(emptyList())
                )
            }
        } catch (_: Exception) {
            if (retryCount < 2) {
                loadRelationsWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    relatedAnime = LoadingState.Success(emptyList())
                )
            }
        }
    }

    private suspend fun loadRecommendationsWithRetry(animeId: Int, retryCount: Int = 0) {
        try {
            delay(retryCount * 1000L)
//            val response = ApiClient.jikanApi.getAnimeRecommendations(animeId)
            val response = ApiClient.jikanApi.getAnimeRecommendations(animeId)

            if (response.isSuccessful) {
                val recommendations = response.body()?.data?.take(8)?.map { rec ->
                    RecommendedAnime(
                        id = rec.entry.malId,
                        title = rec.entry.title,
                        imageUrl = rec.entry.images.jpg.imageUrl
                    )
                } ?: emptyList()

                screenState = screenState.copy(
                    recommendations = LoadingState.Success(recommendations)
                )
            } else if (retryCount < 2) {
                loadRecommendationsWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    recommendations = LoadingState.Success(emptyList())
                )
            }
        } catch (_: Exception) {
            if (retryCount < 2) {
                loadRecommendationsWithRetry(animeId, retryCount + 1)
            } else {
                screenState = screenState.copy(
                    recommendations = LoadingState.Success(emptyList())
                )
            }
        }
    }

    fun retrySection(section: String) {
        when (section) {
            "details" -> {
                screenState = screenState.copy(animeDetail = LoadingState.Loading)
                viewModelScope.launch { loadBasicDetails(currentAnimeId) }
            }
            "characters" -> {
                screenState = screenState.copy(characters = LoadingState.Loading)
                viewModelScope.launch { loadCharactersWithRetry(currentAnimeId) }
            }
            "staff" -> {
                screenState = screenState.copy(staff = LoadingState.Loading)
                viewModelScope.launch { loadStaffWithRetry(currentAnimeId) }
            }
        }
    }

    fun retryAll() {
        loadAnimeDetails(currentAnimeId)
    }
}