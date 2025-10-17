package com.ruhaan.otakuclick.ui.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

// Utility for generating dynamic colors for genre cards
object ColorUtils {

    // Predefined color palette for genres (Spotify-style vibrant colors)
    private val genreColors = listOf(
        Color(0xFF1DB954), // Spotify Green
        Color(0xFF1E3264), // Deep Blue
        Color(0xFF8E44AD), // Purple
        Color(0xFFE67E22), // Orange
        Color(0xFFE74C3C), // Red
        Color(0xFF3498DB), // Light Blue
        Color(0xFF2ECC71), // Emerald
        Color(0xFFF39C12), // Yellow
        Color(0xFF9B59B6), // Violet
        Color(0xFF16A085), // Turquoise
        Color(0xFFE91E63), // Pink
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Amber
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF009688), // Teal
        Color(0xFFCDDC39)  // Lime
    )

    // Generate consistent color for a genre name
    fun getColorForGenre(genreName: String): Color {
        // Use genre name hash to consistently get same color
        val hash = abs(genreName.hashCode())
        val colorIndex = hash % genreColors.size
        return genreColors[colorIndex]
    }

    // Generate slightly darker version for pressed/selected states
    fun getDarkerColor(color: Color): Color {
        return Color(
            red = (color.red * 0.8f).coerceIn(0f, 1f),
            green = (color.green * 0.8f).coerceIn(0f, 1f),
            blue = (color.blue * 0.8f).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }

    // Check if color is light or dark to determine text color
    fun isColorLight(color: Color): Boolean {
        val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
        return luminance > 0.5f
    }

    // Get appropriate text color (black for light backgrounds, white for dark)
    fun getTextColor(backgroundColor: Color): Color {
        return if (isColorLight(backgroundColor)) Color.Black else Color.White
    }
}