package com.ruhaan.otakuclick.ui.screens.detail


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ruhaan.otakuclick.data.LoadingState
import com.ruhaan.otakuclick.data.models.RecommendedAnime
import com.ruhaan.otakuclick.data.models.RelatedAnime
import com.ruhaan.otakuclick.ui.components.common.CharacterGridSection
import com.ruhaan.otakuclick.ui.components.common.StaffSection


// Anime detail screen with poster layout (no banner)
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    animeId: Int,
    onBackClick: () -> Unit = {},
    onAnimeClick: (Int) -> Unit = {},
    viewModel: AnimeDetailViewModel = viewModel()
) {
    // Load anime details
    LaunchedEffect(animeId) {
        viewModel.loadAnimeDetails(animeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                val title = when (val animeState = viewModel.screenState.animeDetail) {
                    is LoadingState.Success -> animeState.data.title
                    else -> "Loading..."
                }
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
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

        // Main content based on loading state
        when (val animeState = viewModel.screenState.animeDetail) {
            is LoadingState.Loading -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading anime details...",
                            color = Color(0xFF8A8A8A)
                        )
                    }
                }
            }

            is LoadingState.Error -> {
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
                            text = "Error loading anime details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = animeState.message,
                            fontSize = 12.sp,
                            color = Color(0xFF8A8A8A),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retrySection("details") }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is LoadingState.Success -> {
                val anime = animeState.data

                // Success state - show anime details with poster layout
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Main info section with poster (NO BANNER)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Anime poster on the left
                                Card(
                                    modifier = Modifier.size(width = 120.dp, height = 160.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    AsyncImage(
                                        model = anime.imageUrl,
                                        contentDescription = anime.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Anime info on the right
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Title
                                    Text(
                                        text = anime.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 22.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Rating
                                    if (anime.score > 0.0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "★",
                                                fontSize = 16.sp,
                                                color = Color(0xFF64B5F6)
                                            )
                                            Text(
                                                text = " ${String.format("%.1f", anime.score)}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    // Episodes
                                    if (anime.episodes > 0) {
                                        InfoChip("${anime.episodes} episodes")
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    // Status
                                    if (anime.status.isNotBlank()) {
                                        InfoChip(anime.status)
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    // Year
                                    if (anime.year != null) {
                                        InfoChip(anime.year.toString())
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    // Season
                                    if (!anime.season.isNullOrBlank()) {
                                        InfoChip(anime.season.replaceFirstChar { it.titlecase() })
                                    }
                                }
                            }
                        }
                    }

                    // Genres section
                    if (anime.genres.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Genres",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    // Genre chips in rows
                                    val genreChunks = anime.genres.chunked(3) // 3 per row
                                    genreChunks.forEach { rowGenres ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowGenres.forEach { genre ->
                                                GenreChip(genre)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Synopsis section
                    if (anime.synopsis.isNotBlank() && anime.synopsis != "No synopsis available") {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Synopsis",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Text(
                                        text = anime.synopsis,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp,
                                        textAlign = TextAlign.Justify
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }

                    // Characters section
                    item {
                        when (val charactersState = viewModel.screenState.characters) {
                            is LoadingState.Loading -> {
                                SimpleLoadingCard("Loading characters...")
                            }
                            is LoadingState.Error -> {
                                SimpleErrorCard("Failed to load characters") {
                                    viewModel.retrySection("characters")
                                }
                            }
                            is LoadingState.Success -> {
                                if (charactersState.data.isNotEmpty()) {
                                        CharacterGridSection(characters = charactersState.data)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Staff section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(), // Card fills the LazyColumn's padded space
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ){
                            when (val staffState = viewModel.screenState.staff) {
                                is LoadingState.Loading -> {
                                    SimpleLoadingCard("Loading staff...")
                                }
                                is LoadingState.Error -> {
                                    SimpleErrorCard("Failed to load staff") {
                                        viewModel.retrySection("staff")
                                    }
                                }
                                is LoadingState.Success -> {
                                    if (staffState.data.isNotEmpty()) {
                                        StaffSection(staff = staffState.data)
                                    }
                                }
                            }
                        }

                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // Related anime section
                    item {
                        when (val relatedState = viewModel.screenState.relatedAnime) {
                            is LoadingState.Loading -> {
                                SimpleLoadingCard("Loading related anime...")
                            }
                            is LoadingState.Error -> {
                                SimpleErrorCard("Failed to load related anime") {
                                    viewModel.retryAll()
                                }
                            }
                            is LoadingState.Success -> {
                                if (relatedState.data.isNotEmpty()) {
                                    RelatedAnimeCard(
                                        relatedAnime = relatedState.data,
                                        onAnimeClick = onAnimeClick  // Now it's used!
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Recommendations section
                    item {
                        when (val recommendationsState = viewModel.screenState.recommendations) {
                            is LoadingState.Loading -> {
                                SimpleLoadingCard("Loading recommendations...")
                            }
                            is LoadingState.Error -> {
                                SimpleErrorCard("Failed to load recommendations") {
                                    viewModel.retryAll()
                                }
                            }
                            is LoadingState.Success -> {
                                if (recommendationsState.data.isNotEmpty()) {
                                        RecommendationsCard(
                                            recommendations = recommendationsState.data,
                                            onAnimeClick = onAnimeClick
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Small info chip component
@Composable
private fun InfoChip(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Genre chip component (simple version without navigation for now)
@Composable
private fun GenreChip(genreName: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF64B5F6).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = genreName,
            fontSize = 12.sp,
            color = Color(0xFF64B5F6),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SimpleLoadingCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(message, color = Color(0xFF8A8A8A))
            }
        }
    }
}

@Composable
private fun SimpleErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, color = Color(0xFF8A8A8A))
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}



@Composable
private fun RelatedAnimeCard(
    relatedAnime: List<RelatedAnime>,
    onAnimeClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Related Anime",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Horizontal scrollable row of poster cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(relatedAnime.size) { index ->
                    val anime = relatedAnime[index]
                    RelatedAnimePosterCard(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) }
                    )
                }
            }
        }
    }
}

// Individual poster card for related anime
@Composable
private fun RelatedAnimePosterCard(
    anime: RelatedAnime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Poster image placeholder (since RelatedAnime might not have imageUrl)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                // If you have imageUrl in RelatedAnime model, use AsyncImage:
                // AsyncImage(
                //     model = anime.imageUrl,
                //     contentDescription = anime.title,
                //     modifier = Modifier.fillMaxSize(),
                //     contentScale = ContentScale.Crop
                // )

                // For now, show a placeholder with first letter
                Text(
                    text = anime.title.firstOrNull()?.toString() ?: "?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Title and relation info
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = anime.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Relation type (Sequel, Prequel, etc.)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF64B5F6).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = anime.relation,
                        fontSize = 9.sp,
                        color = Color(0xFF64B5F6),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun RecommendationsCard(
    recommendations: List<RecommendedAnime>,
    onAnimeClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recommendations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Horizontal scrollable row of poster cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(recommendations.size) { index ->
                    val anime = recommendations[index]
                    RecommendationPosterCard(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) }
                    )
                }
            }
        }
    }
}

// Individual poster card for recommendations
@Composable
private fun RecommendationPosterCard(
    anime: RecommendedAnime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Poster image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            ) {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = null,
                    error = null
                )
            }

            // Title
            Text(
                text = anime.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}






//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AnimeDetailScreen(
//    animeId: Int,
//    onBackClick: () -> Unit,
//    onAnimeClick: (Int) -> Unit = {},
//    viewModel: AnimeDetailViewModel = viewModel()
//) {
//    LaunchedEffect(animeId) {
//        viewModel.loadAnimeDetails(animeId)
//    }
//
//    val screenState = viewModel.screenState
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        TopAppBar(
//            title = {
//                Text(
//                    when (val detailState = screenState.animeDetail) {
//                        is LoadingState.Success -> detailState.data.title
//                        is LoadingState.Loading -> "Loading..."
//                        is LoadingState.Error -> "Error"
//                    },
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    fontSize = 18.sp
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = onBackClick) {
//                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.surface
//            )
//        )
//
//        PullToRefreshContainer(
//            isRefreshing = screenState.isAnyLoading,
//            onRefresh = { viewModel.retryAll() }
//        ) {
//            when (val detailState = screenState.animeDetail) {
//                is LoadingState.Loading -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            CircularProgressIndicator()
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Text("Loading anime details...")
//                        }
//                    }
//                }
//
//                is LoadingState.Error -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(
//                                text = detailState.message,
//                                color = Color.Red
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(onClick = { viewModel.retrySection("details") }) {
//                                Text("Retry")
//                            }
//                        }
//                    }
//                }
//
//                is LoadingState.Success -> {
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        item {
//                            HeroSection(anime = detailState.data)
//                        }
//
//                        item {
//                            InfoSection(anime = detailState.data)
//                        }
//
//                        item {
//                            when (val charactersState = screenState.characters) {
//                                is LoadingState.Loading -> {
//                                    SectionLoadingCard("characters", 300)
//                                }
//
//                                is LoadingState.Error -> {
//                                    SectionErrorCard(
//                                        title = "characters",
//                                        error = charactersState.message,
//                                        onRetry = { viewModel.retrySection("characters") }
//                                    )
//                                }
//
//                                is LoadingState.Success -> {
//                                    if (charactersState.data.isNotEmpty()) {
//                                        CharacterGridSection(characters = charactersState.data)
//                                    }
//                                }
//                            }
//                        }
//
//                        item {
//                            when (val staffState = screenState.staff) {
//                                is LoadingState.Loading -> {
//                                    SectionLoadingCard("staff", 200)
//                                }
//
//                                is LoadingState.Error -> {
//                                    SectionErrorCard(
//                                        title = "staff",
//                                        error = staffState.message,
//                                        onRetry = { viewModel.retrySection("staff") }
//                                    )
//                                }
//
//                                is LoadingState.Success -> {
//                                    if (staffState.data.isNotEmpty()) {
//                                        StaffSection(staff = staffState.data)
//                                    }
//                                }
//                            }
//                        }
//
//                        item {
//                            when (val relatedState = screenState.relatedAnime) {
//                                is LoadingState.Success -> {
//                                    if (relatedState.data.isNotEmpty()) {
//                                        RelatedAnimeSection(
//                                            relatedAnime = relatedState.data,
//                                            onAnimeClick = onAnimeClick
//                                        )
//                                    }
//                                }
//
//                                else -> { /* Don't show loading for related anime */
//                                }
//                            }
//                        }
//
//                        item {
//                            when (val recommendationsState = screenState.recommendations) {
//                                is LoadingState.Success -> {
//                                    if (recommendationsState.data.isNotEmpty()) {
//                                        RecommendationsSection(
//                                            recommendations = recommendationsState.data,
//                                            onAnimeClick = onAnimeClick
//                                        )
//                                    }
//                                }
//
//                                else -> { /* Don't show loading for recommendations */
//                                }
//                            }
//                        }
//
//                        item {
//                            Spacer(modifier = Modifier.height(32.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}