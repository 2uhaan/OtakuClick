package com.ruhaan.otakuclick.ui.components.cards

import android.R.attr.end
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.ruhaan.otakuclick.data.models.CharacterItem

@Composable
fun CharacterCard(character: CharacterItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .width(150.dp)
            .padding(end = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                onError = {
                    // Handle image loading error
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = character.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            if (character.voiceActor != null) {
                Spacer(modifier = Modifier.height(6.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF2A2A2A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Voice Actor",
                            fontSize = 10.sp,
                            color = Color(0xFF8A8A8A),
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = character.voiceActor,
                            fontSize = 12.sp,
                            color = Color(0xFF64B5F6),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )

                        if (character.language != null) {
                            Text(
                                text = character.language,
                                fontSize = 10.sp,
                                color = Color(0xFF6A6A6A),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}