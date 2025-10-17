package com.ruhaan.otakuclick.ui.screens.club

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruhaan.otakuclick.data.models.AuthResult
import com.ruhaan.otakuclick.ui.components.club.ClubPostCard
import com.ruhaan.otakuclick.ui.components.club.ComposePostDialog
import com.ruhaan.otakuclick.ui.components.club.UserColorCircle
import com.ruhaan.otakuclick.ui.screens.auth.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    clubViewModel: ClubViewModel = viewModel(),
) {

    val authState = authViewModel.authState
    val feedState = clubViewModel.feedState
    val composeState = clubViewModel.composeState

    val context = LocalContext.current // Add this line

    // State for compose dialog
    var showComposeDialog by remember { mutableStateOf(false) }

    // Set current user in club view model
    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            clubViewModel.setCurrentUser(authState.user)
        }
    }

    // Get current user
    val currentUser = if (authState is AuthResult.Success) authState.user else null

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎭",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Anime Club",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                // Refresh button
                IconButton(
                    onClick = { clubViewModel.retryLoadPosts() }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // LinkedIn/Facebook style: Input at top
        if (currentUser != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // User avatar
                    UserColorCircle(
                        username = currentUser.username,
                        size = 40.dp
                    )

                    // Fake input that opens compose dialog
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showComposeDialog = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "What's happening in the anime world?",
                            color = Color(0xFF8A8A8A),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // Feed content
        when {
            // Loading state
            feedState.isLoading && feedState.posts.isEmpty() -> {
                LoadingState()
            }

            // Error state
            feedState.error != null && feedState.posts.isEmpty() -> {
                ErrorState(
                    error = feedState.error,
                    onRetry = { clubViewModel.retryLoadPosts() }
                )
            }

            // Empty state
            feedState.posts.isEmpty() -> {
                EmptyState(
                    onStartPosting = { showComposeDialog = true }
                )
            }

            // Success state with posts
            else -> {
                PostsFeed(
                    posts = feedState.posts,
                    isRefreshing = feedState.isLoading
                )
            }
        }

        // Compose post dialog (keep existing)
        if (currentUser != null) {
            ComposePostDialog(
                isVisible = showComposeDialog,
                composeState = composeState,
                username = currentUser.username,
                onTextChange = { text ->
                    clubViewModel.updateComposeText(text)
                },
                onPost = {
                    clubViewModel.createPost(context = context)
                    showComposeDialog = false
                },
                onDismiss = {
                    showComposeDialog = false
                    clubViewModel.clearComposeError()
                }
            )
        }
    }
}


// Main Club screen with Threads-style feed
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ClubScreen(
//    modifier: Modifier = Modifier,
//    authViewModel: AuthViewModel,
//    clubViewModel: ClubViewModel = viewModel(),
//) {
//    val authState = authViewModel.authState
//    val feedState = clubViewModel.feedState
//    val composeState = clubViewModel.composeState
//
//    // State for compose dialog
//    var showComposeDialog by remember { mutableStateOf(false) }
//
//    // Set current user in club view model
//    LaunchedEffect(authState) {
//        if (authState is AuthResult.Success) {
//            clubViewModel.setCurrentUser(authState.user)
//        }
//    }
//
//    // Get current user
//    val currentUser = if (authState is AuthResult.Success) authState.user else null
//
//    Box(
//        modifier = modifier.fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Header
//            TopAppBar(
//                title = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "🎭",
//                            fontSize = 20.sp
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Club",
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//                },
//                actions = {
//                    // Refresh button
//                    IconButton(
//                        onClick = { clubViewModel.retryLoadPosts() }
//                    ) {
//                        Icon(
//                            Icons.Default.Refresh,
//                            contentDescription = "Refresh",
//                            tint = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    titleContentColor = MaterialTheme.colorScheme.onSurface
//                )
//            )
//
//            // Feed content
//            when {
//                // Loading state
//                feedState.isLoading && feedState.posts.isEmpty() -> {
//                    LoadingState()
//                }
//
//                // Error state
//                feedState.error != null && feedState.posts.isEmpty() -> {
//                    ErrorState(
//                        error = feedState.error,
//                        onRetry = { clubViewModel.retryLoadPosts() }
//                    )
//                }
//
//                // Empty state
//                feedState.posts.isEmpty() -> {
//                    EmptyState(
//                        onStartPosting = { showComposeDialog = true }
//                    )
//                }
//
//                // Success state with posts
//                else -> {
//                    PostsFeed(
//                        posts = feedState.posts,
//                        isRefreshing = feedState.isLoading,
////                        onRefresh = { clubViewModel.retryLoadPosts() }
//                    )
//                }
//            }
//        }
//
//        // Floating compose button (Threads style)
//        if (currentUser != null) {
//            FloatingActionButton(
//                onClick = { showComposeDialog = true },
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(20.dp),
//                containerColor = MaterialTheme.colorScheme.primary,
//                contentColor = Color.White,
//                shape = CircleShape
//            ) {
//                Icon(
//                    Icons.Default.Add,
//                    contentDescription = "Compose post",
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//        }
//
//        // Compose post dialog
//        if (currentUser != null) {
//            ComposePostDialog(
//                isVisible = showComposeDialog,
//                composeState = composeState,
//                username = currentUser.username,
//                onTextChange = { text ->
//                    clubViewModel.updateComposeText(text)
//                },
//                onPost = {
//                    clubViewModel.createPost()
//                    showComposeDialog = false
//                },
//                onDismiss = {
//                    showComposeDialog = false
//                    clubViewModel.clearComposeError()
//                }
//            )
//        }
//    }
//}

// Posts feed with pull-to-refresh
@Composable
private fun PostsFeed(
    posts: List<com.ruhaan.otakuclick.data.models.ClubPost>,
    isRefreshing: Boolean,
//    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pull to refresh indicator
        if (isRefreshing) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Posts
        items(
            items = posts,
            key = { post -> post.id }
        ) { post ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(post.id) {
                visible = true
            }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(
                            initialOffsetY = { -it/3 },
                            animationSpec = tween(300)
                        )
            ) {
                ClubPostCard(post = post)
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// Loading state
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading posts...",
                color = Color(0xFF8A8A8A),
                fontSize = 14.sp
            )
        }
    }
}

// Error state
@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "😵",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops! Something went wrong",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Try Again")
            }
        }
    }
}

// Empty state
@Composable
private fun EmptyState(
    onStartPosting: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "🎭",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to Club!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Share your anime thoughts\nand connect with the community",
                fontSize = 14.sp,
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStartPosting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Posting")
            }
        }
    }
}