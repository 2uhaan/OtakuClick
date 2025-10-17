package com.ruhaan.otakuclick.ui.screens.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruhaan.otakuclick.ui.components.cards.PosterCard
import com.ruhaan.otakuclick.ui.components.common.ShimmerPosterCard


// Simplified explore screen that hides when search is active
@Composable
fun ExploreScreen(
    onAnimeClick: (Int) -> Unit = {},
    isSearchActive: Boolean = false,           // New parameter to track search state
    exploreViewModel: ExploreViewModel = viewModel()
) {
    // Only show trending content when search is not active
    AnimatedVisibility(
        visible = !isSearchActive,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Section header
            Text(
                text = "Trending Now",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                exploreViewModel.isLoading -> {
                    // Beautiful shimmer loading state
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(9) { // Show 9 shimmer cards (3x3 grid)
                            ShimmerPosterCard()
                        }
                    }
                }


                exploreViewModel.errorMessage != null -> {
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
                                text = "Failed to load trending anime",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = exploreViewModel.errorMessage!!,
                                fontSize = 12.sp,
                                color = Color(0xFF8A8A8A),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { exploreViewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                exploreViewModel.trendingAnime.isNotEmpty() -> {
                    // Success state - show trending grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(exploreViewModel.trendingAnime) { anime ->
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
                                text = "No trending anime found",
                                color = Color(0xFF8A8A8A)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { exploreViewModel.retry() }) {
                                Text("Refresh")
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun ExploreScreen(
//    onAnimeClick: (Int) -> Unit = {},
//    searchViewModel: SearchViewModel = viewModel(),      // Keep search functionality
//    exploreViewModel: ExploreViewModel = viewModel()    // Add trending functionality
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        // Show search results if user has searched
//        if (searchViewModel.animeList.isNotEmpty() || searchViewModel.isLoading || searchViewModel.errorMessage != null) {
//            SearchResultsSection(
//                searchViewModel = searchViewModel,
//                onAnimeClick = onAnimeClick
//            )
//        } else {
//            // Show trending anime grid when not searching
//            TrendingSection(
//                exploreViewModel = exploreViewModel,
//                onAnimeClick = onAnimeClick
//            )
//        }
//    }
//}
//
//// Search results section (your existing search functionality)
//@Composable
//private fun SearchResultsSection(
//    searchViewModel: SearchViewModel,
//    onAnimeClick: (Int) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Search Results",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        when {
//            searchViewModel.isLoading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//
//            searchViewModel.errorMessage != null -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = searchViewModel.errorMessage!!,
//                            color = MaterialTheme.colorScheme.error
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = { searchViewModel.clearError() }) {
//                            Text("Clear")
//                        }
//                    }
//                }
//            }
//
//            else -> {
//                // Search results in grid format
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),           // 2 columns for search results
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    contentPadding = PaddingValues(bottom = 16.dp)
//                ) {
//                    items(searchViewModel.animeList) { anime ->
//                        PosterCard(
//                            title = anime.title,
//                            imageUrl = anime.imageUrl,
//                            year = null,                     // Search results don't need year
//                            rating = if (anime.score > 0) anime.score else null,
//                            onClick = { onAnimeClick(anime.id) }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun TrendingSection(
//    exploreViewModel: ExploreViewModel,
//    onAnimeClick: (Int) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Section header
//        Text(
//            text = "Trending Now",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        when {
//            exploreViewModel.isLoading -> {
//                // Loading state
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        CircularProgressIndicator()
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "Loading trending anime...",
//                            color = Color(0xFF8A8A8A)
//                        )
//                    }
//                }
//            }
//
//            exploreViewModel.errorMessage != null -> {
//                // Error state
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = "😵",
//                            fontSize = 48.sp
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "Failed to load trending anime",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = MaterialTheme.colorScheme.onBackground
//                        )
//                        Text(
//                            text = exploreViewModel.errorMessage!!,
//                            fontSize = 12.sp,
//                            color = Color(0xFF8A8A8A),
//                            modifier = Modifier.padding(top = 4.dp)
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(onClick = { exploreViewModel.retry() }) {
//                            Text("Retry")
//                        }
//                    }
//                }
//            }
//
//            exploreViewModel.trendingAnime.isNotEmpty() -> {
//                // Success state - show trending grid
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(3),           // 3 columns for compact poster view
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    contentPadding = PaddingValues(bottom = 16.dp)
//                ) {
//                    items(exploreViewModel.trendingAnime) { anime ->
//                        PosterCard(
//                            title = anime.title,
//                            imageUrl = anime.imageUrl,
//                            year = anime.year,
//                            rating = anime.rating,
//                            onClick = { onAnimeClick(anime.id) }
//                        )
//                    }
//                }
//            }
//
//            else -> {
//                // Empty state
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = "📺",
//                            fontSize = 48.sp
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "No trending anime found",
//                            color = Color(0xFF8A8A8A)
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = { exploreViewModel.retry() }) {
//                            Text("Refresh")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}