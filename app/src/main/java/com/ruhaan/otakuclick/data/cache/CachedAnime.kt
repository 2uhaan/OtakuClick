package com.ruhaan.otakuclick.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_anime")
data class CachedAnime(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double?,
    val year: String?,
    val status: String?,
    val cacheType: String, // "trending", "genre_1", "genre_5", etc.
    val cachedAt: Long = System.currentTimeMillis()
)