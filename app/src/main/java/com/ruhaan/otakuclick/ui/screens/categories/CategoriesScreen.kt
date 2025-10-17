package com.ruhaan.otakuclick.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruhaan.otakuclick.ui.components.cards.GenreCard

// Categories screen with Spotify-style genre cards
@Composable
fun CategoriesScreen(
    onGenreClick: (Int, String) -> Unit = { _, _ -> },   // Callback for genre navigation
    viewModel: CategoriesViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Screen header
        Text(
            text = "Categories",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        when {
            viewModel.isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading genres...",
                            color = Color(0xFF8A8A8A),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            viewModel.errorMessage != null -> {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎭",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Failed to load categories",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = viewModel.errorMessage!!,
                            fontSize = 12.sp,
                            color = Color(0xFF8A8A8A),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            viewModel.genreList.isNotEmpty() -> {
                // Success state - show genre grid
                Column {
                    // Subtitle with count
                    Text(
                        text = "${viewModel.genreList.size} genres available",
                        fontSize = 14.sp,
                        color = Color(0xFF8A8A8A),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Genre cards grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),           // 2 columns for genre cards
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(viewModel.genreList) { genre ->
                            GenreCard(
                                genre = genre,
                                onClick = {
                                    onGenreClick(genre.id, genre.name)  // Navigate to genre detail
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                // Empty state (shouldn't happen with API but good to have)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎭",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No genres available",
                            fontSize = 16.sp,
                            color = Color(0xFF8A8A8A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Refresh")
                        }
                    }
                }
            }
        }
    }
}