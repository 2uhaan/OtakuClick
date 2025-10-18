package com.ruhaan.otakuclick.data.cache

import androidx.room.*

@Dao
interface AnimeDao {

    // Get cached anime by type (trending, genre, etc.)
    @Query("SELECT * FROM cached_anime WHERE cacheType = :type")
    suspend fun getCachedAnime(type: String): List<CachedAnime>

    // Save anime to cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: List<CachedAnime>)

    // Clear old cache (older than 10 minutes)
    @Query("DELETE FROM cached_anime WHERE cachedAt < :cutoffTime")
    suspend fun clearOldCache(cutoffTime: Long)

    // Clear specific cache type
    @Query("DELETE FROM cached_anime WHERE cacheType = :type")
    suspend fun clearCacheType(type: String)
}
