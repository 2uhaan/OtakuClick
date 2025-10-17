package com.ruhaan.otakuclick.navigation


object Routes {
    // Main bottom navigation tabs
    const val EXPLORE = "explore"
    const val CATEGORIES = "categories"
    const val SCHEDULE = "schedule"

    const val CLUB = "club"



    // Detail screens (from Project 2)
    const val ANIME_DETAIL = "anime_detail/{animeId}"

    // Genre and studio detail screens (for later chapters)
    const val GENRE_DETAIL = "genre_detail/{genreId}/{genreName}"

    // Authentication routes
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    // Helper functions for parameterized routes
    fun animeDetail(animeId: Int): String {
        return "anime_detail/$animeId"
    }

    fun genreDetail(genreId: Int, genreName: String): String {
        return "genre_detail/$genreId/$genreName"
    }


}