package com.ruhaan.otakuclick.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ruhaan.otakuclick.data.models.AnimeItem

// Compact dropdown search results
@Composable
fun CompactSearchResults(
    modifier: Modifier = Modifier,
    searchResults: List<AnimeItem>,
    isLoading: Boolean,
    errorMessage: String?,
    onAnimeClick: (Int) -> Unit = {},
    onRetry: () -> Unit = {},

) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)  // Higher elevation for dropdown
    ) {
        when {
            isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Searching...",
                            fontSize = 14.sp,
                            color = Color(0xFF8A8A8A)
                        )
                    }
                }
            }

            errorMessage != null -> {
                // Error state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Search failed",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFF8A8A8A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onRetry) {
                            Text("Retry", fontSize = 12.sp)
                        }
                    }
                }
            }

            searchResults.isNotEmpty() -> {
                // Results list
                Column {
                    // Results header
                    Text(
                        text = "${searchResults.size} results",
                        fontSize = 12.sp,
                        color = Color(0xFF8A8A8A),
                        modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 8.dp)
                    )

                    // Compact results list
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)  // Limit dropdown height
                    ) {
                        items(searchResults) { anime ->
                            CompactAnimeItem(
                                anime = anime,
                                onClick = { onAnimeClick(anime.id) }
                            )
                        }
                    }
                }
            }

            else -> {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        fontSize = 14.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }
            }
        }
    }
}

// Compact anime item for search results
@Composable
private fun CompactAnimeItem(
    anime: AnimeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small anime poster
        AsyncImage(
            model = anime.imageUrl,
            contentDescription = anime.title,
            modifier = Modifier
                .size(40.dp, 56.dp)             // Small poster size
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Anime info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = anime.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (anime.score > 0.0) {
                    Text(
                        text = "★ ${anime.score}",
                        fontSize = 11.sp,
                        color = Color(0xFF64B5F6)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = "${anime.episodes} episodes",
                    fontSize = 11.sp,
                    color = Color(0xFF8A8A8A)
                )
            }
        }
    }
}