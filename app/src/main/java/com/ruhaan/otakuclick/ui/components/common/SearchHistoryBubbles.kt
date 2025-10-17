package com.ruhaan.otakuclick.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Search history displayed as small bubbles
@Composable
fun SearchHistoryBubbles(
    modifier: Modifier = Modifier,
    searchHistory: List<String>,
    onHistoryClick: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
) {
    if (searchHistory.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Header with clear option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent searches",
                    fontSize = 12.sp,
                    color = Color(0xFF8A8A8A),
                    fontWeight = FontWeight.Medium
                )

                TextButton(
                    onClick = onClearHistory,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Clear",
                        fontSize = 11.sp,
                        color = Color(0xFF64B5F6)
                    )
                }
            }

            // Bubbles row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                items(searchHistory) { searchTerm ->
                    SearchBubble(
                        text = searchTerm,
                        onClick = { onHistoryClick(searchTerm) }
                    )
                }
            }
        }
    }
}

// Individual search bubble component
@Composable
private fun SearchBubble(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),         // Fully rounded bubble shape
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)     // Dark gray bubble
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}