package com.ruhaan.otakuclick.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.otakuclick.data.models.AnimeDetail

@Composable
fun InfoSection(anime: AnimeDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Synopsis",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.synopsis,
                fontSize = 14.sp,
                color = Color(0xFFB3B3B3),
                lineHeight = 20.sp
            )

            if (anime.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Genres",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = anime.genres.joinToString(" • "),
                    fontSize = 14.sp,
                    color = Color(0xFF64B5F6)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Status",
                        fontSize = 14.sp,
                        color = Color(0xFF8A8A8A)
                    )
                    Text(
                        text = anime.status,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (anime.year != null) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Year",
                            fontSize = 14.sp,
                            color = Color(0xFF8A8A8A)
                        )
                        Text(
                            text = "${anime.year}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (anime.studios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Studios",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A)
                )
                Text(
                    text = anime.studios.joinToString(", "),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}