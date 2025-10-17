package com.ruhaan.otakuclick.data.models


// UI model for schedule items
data class ScheduleItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val episode: Int?,                      // Current/next episode number
    val airDay: String?,                    // "Monday", "Tuesday", etc.
    val airTime: String?,                   // "11:00 PM" format
    val status: String?,                    // "Airing", "Upcoming", etc.
    val rating: Double?,
    val year: Int?,
    val isToday: Boolean = false,           // Whether it airs today
    val isUpcoming: Boolean = false         // Whether it's upcoming
)

