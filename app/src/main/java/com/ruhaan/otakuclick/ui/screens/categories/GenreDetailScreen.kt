package com.ruhaan.otakuclick.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruhaan.otakuclick.ui.components.cards.PosterCard
import com.ruhaan.otakuclick.ui.components.common.ShimmerPosterCard
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton

// Individual genre detail page showing anime in that genre
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailScreen(
    genreId: Int,
    genreName: String,
    onBackClick: () -> Unit = {},
    onAnimeClick: (Int) -> Unit = {},
    viewModel: GenreDetailViewModel = viewModel()
) {
    // Load genre anime when screen appears
    LaunchedEffect(genreId) {
        viewModel.loadGenreAnime(genreId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = genreName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                viewModel.isLoading -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(9) { // Show 9 shimmer cards
                            ShimmerPosterCard()
                        }
                    }
                    // Loading state
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            CircularProgressIndicator()
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Text(
//                                text = "Loading $genreName anime...",
//                                color = Color(0xFF8A8A8A)
//                            )
//                        }
//                    }
                }

                viewModel.errorMessage != null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "😵",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Failed to load $genreName anime",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = viewModel.errorMessage!!,
                                fontSize = 12.sp,
                                color = Color(0xFF8A8A8A),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row {
                                Button(onClick = { viewModel.retry() }) {
                                    Text("Retry")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(onClick = { viewModel.forceRefresh() }) {
                                    Text("Force Refresh")
                                }
                            }

//                            Button(onClick = { viewModel.loadGenreAnime(genreId) }) {
//                                Text("Retry")
//                            }
                        }
                    }
                }

                viewModel.animeList.isNotEmpty() -> {
                    // Success state - show anime grid
                    Column {
                        // Results counter
                        Text(
                            text = "${viewModel.animeList.size} ${genreName.lowercase()} anime",
                            fontSize = 14.sp,
                            color = Color(0xFF8A8A8A),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Anime grid (same style as Explore page)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),       // 3 columns for compact view
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(viewModel.animeList) { anime ->
                                PosterCard(
                                    title = anime.title,
                                    imageUrl = anime.imageUrl,
                                    year = anime.year,
                                    rating = anime.rating,
                                    onClick = { onAnimeClick(anime.id) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📺",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No $genreName anime found",
                                fontSize = 16.sp,
                                color = Color(0xFF8A8A8A)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.loadGenreAnime(genreId) }) {
                                Text("Refresh")
                            }
                        }
                    }
                }
            }
        }
    }
}