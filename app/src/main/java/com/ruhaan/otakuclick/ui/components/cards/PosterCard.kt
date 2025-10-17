package com.ruhaan.otakuclick.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@SuppressLint("DefaultLocale")
@Composable
fun PosterCard(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String,
    year: String? = null,
    rating: Double? = null,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp),                    // Small padding between cards
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main poster image
        Card(
            modifier = Modifier
                .aspectRatio(2f / 3f)          // Standard poster aspect ratio (width:height = 2:3)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                // Poster image
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop   // Fill card while maintaining aspect ratio
                )

                // Rating overlay (top-right corner)
                if (rating != null && rating > 0.0) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)  // Semi-transparent black
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "★ ${String.format("%.1f", rating)}",
                            color = Color(0xFF64B5F6),         // Your accent blue
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title and year below poster
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Anime title
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,                          // Allow 2 lines for longer titles
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )

            // Year (if available)
            if (year != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = year,
                    fontSize = 10.sp,
                    color = Color(0xFF8A8A8A),         // Gray text for year
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}