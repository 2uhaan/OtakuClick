package com.ruhaan.otakuclick.data.api

import com.ruhaan.otakuclick.data.models.AnimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface JikanApiService {

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): Response<AnimeResponse>

    @GET("anime/{id}/full")
    suspend fun getAnimeDetails(
        @Path("id") animeId: Int
    ): Response<DetailedAnimeResponse>

    @GET("anime/{id}/characters")
    suspend fun getAnimeCharacters(
        @Path("id") animeId: Int
    ): Response<CharactersResponse>

    @GET("anime/{id}/staff")
    suspend fun getAnimeStaff(
        @Path("id") animeId: Int
    ): Response<StaffResponse>

    @GET("anime/{id}/recommendations")
    suspend fun getAnimeRecommendations(
        @Path("id") animeId: Int
    ): Response<RecommendationsResponse>



    @GET("top/anime")
    suspend fun getTrendingAnime(
        @Query("limit") limit: Int = 25,      // Number of anime to fetch
        @Query("filter") filter: String = "airing"  // Focus on currently airing anime
    ): Response<TrendingAnimeResponse>


    @GET("seasons/now")
    suspend fun getCurrentSeasonAnime(
        @Query("limit") limit: Int = 25       // Current season anime
    ): Response<TrendingAnimeResponse>

    // Get all available genres
    @GET("genres/anime")
    suspend fun getAnimeGenres(): Response<GenresResponse>

    // Get anime by specific genre
    @GET("anime")
    suspend fun getAnimeByGenre(
        @Query("genres") genreId: Int,
        @Query("limit") limit: Int = 25,
        @Query("order_by") orderBy: String = "score",     // Order by rating
        @Query("sort") sort: String = "desc"              // Highest rated first
    ): Response<GenreAnimeResponse>


    // Get current season's schedule
    @GET("seasons/now")
    suspend fun getCurrentSeasonSchedule(
        @Query("limit") limit: Int = 25
    ): Response<ScheduleResponse>

    // Get schedule by specific day
    @GET("schedules")
    suspend fun getScheduleByDay(
        @Query("filter") day: String = "monday",    // monday, tuesday, etc.
        @Query("limit") limit: Int = 25
    ): Response<ScheduleResponse>

    // Get upcoming anime (alternative endpoint)
    @GET("seasons/upcoming")
    suspend fun getUpcomingAnime(
        @Query("limit") limit: Int = 25
    ): Response<ScheduleResponse>

    @GET("top/anime")
    suspend fun getTopUpcomingAnime(
        @Query("filter") filter: String = "upcoming",
        @Query("limit") limit: Int = 25
    ): Response<ScheduleResponse>



}


