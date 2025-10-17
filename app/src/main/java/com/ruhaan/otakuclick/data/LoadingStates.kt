package com.ruhaan.otakuclick.data

import com.ruhaan.otakuclick.data.models.AnimeDetail
import com.ruhaan.otakuclick.data.models.CharacterItem
import com.ruhaan.otakuclick.data.models.RecommendedAnime
import com.ruhaan.otakuclick.data.models.RelatedAnime
import com.ruhaan.otakuclick.data.models.StaffItem

sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}

data class DetailScreenState(
    val animeDetail: LoadingState<AnimeDetail> = LoadingState.Loading,
    val characters: LoadingState<List<CharacterItem>> = LoadingState.Loading,
    val staff: LoadingState<List<StaffItem>> = LoadingState.Loading,
    val relatedAnime: LoadingState<List<RelatedAnime>> = LoadingState.Loading,
    val recommendations: LoadingState<List<RecommendedAnime>> = LoadingState.Loading
) {
    val isAnyLoading: Boolean
        get() = listOf(animeDetail, characters, staff, relatedAnime, recommendations)
            .any { it is LoadingState.Loading }

    val hasAnyError: Boolean
        get() = listOf(animeDetail, characters, staff, relatedAnime, recommendations)
            .any { it is LoadingState.Error }
}

