package com.ruhaan.otakuclick.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// Simple manager for search history storage
class SearchHistoryManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "anime_search_history",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 8  // Keep last 8 searches
    }

    // Get search history as list
    fun getSearchHistory(): List<String> {
        val historyString = prefs.getString(HISTORY_KEY, "") ?: ""
        return if (historyString.isBlank()) {
            emptyList()
        } else {
            historyString.split(",").filter { it.isNotBlank() }
        }
    }

    // Add new search to history
    fun addSearch(query: String) {
        if (query.isBlank()) return

        val currentHistory = getSearchHistory().toMutableList()

        // Remove if already exists (to move to front)
        currentHistory.remove(query)

        // Add to front
        currentHistory.add(0, query)

        // Keep only recent searches
        val trimmedHistory = currentHistory.take(MAX_HISTORY_SIZE)

        // Save back to preferences
        prefs.edit {
            putString(HISTORY_KEY, trimmedHistory.joinToString(","))
        }
    }

    // Clear all search history
    fun clearHistory() {
        prefs.edit { remove(HISTORY_KEY) }
    }
}