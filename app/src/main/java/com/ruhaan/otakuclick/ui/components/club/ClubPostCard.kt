package com.ruhaan.otakuclick.ui.components.club

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.otakuclick.data.models.ClubPost
import kotlin.math.absoluteValue

// Individual post card with Threads-style design
@Composable
fun ClubPostCard(
    post: ClubPost,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User color circle (like macOS)
            UserColorCircle(
                username = post.username,
                size = 40.dp
            )

            // Post content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Username and time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = formatTimeAgo(post.timestamp),
                        fontSize = 12.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Post text
                Text(
                    text = post.text,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// User color circle (macOS style)
@Composable
fun UserColorCircle(
    username: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val userColor = getUserColor(username)

    Box(
        modifier = modifier
            .size(size)
            .background(
                color = userColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = username.firstOrNull()?.toString()?.uppercase() ?: "?",
            color = Color.White,
            fontSize = (size.value * 0.4).sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Generate consistent color for username
private fun getUserColor(username: String): Color {
    val colors = listOf(
        Color(0xFF007AFF), // Blue
        Color(0xFFFF2D92), // Pink
        Color(0xFF27CA3F), // Green
        Color(0xFFFF9500), // Orange
        Color(0xFFAF52DE), // Purple
        Color(0xFFFF5F56), // Red
        Color(0xFFFFBD2E), // Yellow
        Color(0xFF5856D6)  // Indigo
    )

    val hash = username.hashCode()
    return colors[hash.absoluteValue % colors.size]
}

// Format timestamp to "2h ago" style
fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "now" // Less than 1 minute
        diff < 3_600_000 -> "${diff / 60_000}m" // Less than 1 hour
        diff < 86_400_000 -> "${diff / 3_600_000}h" // Less than 1 day
        diff < 604_800_000 -> "${diff / 86_400_000}d" // Less than 1 week
        else -> "${diff / 604_800_000}w" // Weeks
    }
}