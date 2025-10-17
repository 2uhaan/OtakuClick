package com.ruhaan.otakuclick.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.otakuclick.data.models.Genre
import com.ruhaan.otakuclick.ui.utils.ColorUtils

@Composable
fun GenreCard(
    modifier: Modifier = Modifier,
    genre: Genre,
    onClick: () -> Unit = {},
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)               // Wide rectangular cards like Spotify
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 8.dp else 4.dp    // Elevated when pressed
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Gradient background for visual interest
                    Brush.linearGradient(
                        colors = listOf(
                            genre.color,
                            ColorUtils.getDarkerColor(genre.color)    // Darker gradient
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Genre name
                Text(
                    text = genre.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorUtils.getTextColor(genre.color),    // Auto text color
                    lineHeight = 22.sp,
                    maxLines = 2                     // Handle long genre names
                )

                // Anime count (if available)
                if (genre.animeCount != null && genre.animeCount > 0) {
                    Text(
                        text = "${genre.animeCount} anime",
                        fontSize = 12.sp,
                        color = ColorUtils.getTextColor(genre.color).copy(alpha = 0.8f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Decorative element (optional geometric shape)
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .align(Alignment.TopEnd)
//                    .offset(x = 10.dp, y = (-10).dp)    // Partially outside card
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(
//                        ColorUtils.getTextColor(genre.color).copy(alpha = 0.1f)
//                    )
//            )
        }
    }

    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)       // Brief press animation
            isPressed = false
        }
    }
}