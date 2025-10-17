package com.ruhaan.otakuclick.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ruhaan.otakuclick.data.models.ScheduleItem

// Card for displaying scheduled anime episodes
@SuppressLint("DefaultLocale")
@Composable
fun ScheduleCard(
    modifier: Modifier = Modifier,
    scheduleItem: ScheduleItem,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (scheduleItem.isToday) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // Highlight today's anime
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Anime poster
            Card(
                modifier = Modifier.size(width = 60.dp, height = 80.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                AsyncImage(
                    model = scheduleItem.imageUrl,
                    contentDescription = scheduleItem.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Anime info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = scheduleItem.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Episode info
                if (scheduleItem.episode != null) {
                    Text(
                        text = "Episode ${scheduleItem.episode}",
                        fontSize = 12.sp,
                        color = Color(0xFF64B5F6),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Air time and status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Air time
                    if (scheduleItem.airTime != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = scheduleItem.airTime,
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status indicator (simplified for upcoming)
                    Text(
                        text = "• ${scheduleItem.status ?: "Upcoming"}",
                        fontSize = 11.sp,
                        color = Color(0xFF64B5F6),          // Use accent color for upcoming
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Rating (if available)
            if (scheduleItem.rating != null && scheduleItem.rating > 0.0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "★",
                        fontSize = 16.sp,
                        color = Color(0xFF64B5F6)
                    )
                    Text(
                        text = String.format("%.1f", scheduleItem.rating),
                        fontSize = 11.sp,
                        color = Color(0xFF8A8A8A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}