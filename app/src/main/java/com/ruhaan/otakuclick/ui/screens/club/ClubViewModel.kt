package com.ruhaan.otakuclick.ui.screens.club

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.models.*
import com.ruhaan.otakuclick.data.repository.ClubRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import android.content.Context
import com.ruhaan.otakuclick.data.notifications.ClubNotificationService

class ClubViewModel(
    private val clubRepository: ClubRepository = ClubRepository()
) : ViewModel() {

    // Feed state
    var feedState by mutableStateOf(ClubFeedState())
        private set

    // Compose state
    var composeState by mutableStateOf(ComposePostState())
        private set

    // Current user (will be passed from AuthViewModel)
    private var currentUser: User? = null

    init {
        loadPosts()
        observeRealTimePosts()
    }

    // Set current user
    fun setCurrentUser(user: User) {
        currentUser = user
    }

    // Load posts initially
    private fun loadPosts() {
        feedState = feedState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val result = clubRepository.getPosts()
                result.fold(
                    onSuccess = { posts ->
                        feedState = feedState.copy(
                            posts = posts,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        feedState = feedState.copy(
                            isLoading = false,
                            error = "Failed to load posts: ${exception.message}"
                        )
                    }
                )
            } catch (_: Exception) {
                feedState = feedState.copy(
                    isLoading = false,
                    error = "Network error. Please try again."
                )
            }
        }
    }

    // Observe real-time posts
    private fun observeRealTimePosts() {
        viewModelScope.launch {
            clubRepository.getPostsFlow()
                .catch { exception ->
                    feedState = feedState.copy(
                        error = "Connection lost: ${exception.message}"
                    )
                }
                .collect { posts ->
                    feedState = feedState.copy(
                        posts = posts,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    // Update compose text
    fun updateComposeText(text: String) {
        composeState = composeState.copy(
            text = text,
            characterCount = text.length,
            error = null
        )
    }

    // Create new post
    fun createPost(context: Context? = null) {
        val user = currentUser ?: return
        val text = composeState.text.trim()

        if (text.isEmpty()) {
            composeState = composeState.copy(error = "Post cannot be empty")
            return
        }

        if (text.length > 500) {
            composeState = composeState.copy(error = "Post is too long (max 500 characters)")
            return
        }

        composeState = composeState.copy(isLoading = true, error = null)
        feedState = feedState.copy(isPosting = true)

        viewModelScope.launch {
            try {
                val request = CreatePostRequest(
                    text = text,
                    userId = user.uid,
                    username = user.username
                )

                val result = clubRepository.createPost(request)
                result.fold(
                    onSuccess = { post -> // Change from {} to {post ->}
                        // Clear compose form
                        composeState = ComposePostState()
                        feedState = feedState.copy(isPosting = false)

                        // Trigger local notification for demo/testing
                        context?.let {
                            ClubNotificationService.showLocalNotification(
                                context = it,
                                username = post.username,
                                postText = post.text
                            )
                        }
                    },
                    onFailure = { exception ->
                        composeState = composeState.copy(
                            isLoading = false,
                            error = "Failed to post: ${exception.message}"
                        )
                        feedState = feedState.copy(isPosting = false)
                    }
                )
            } catch (_: Exception) {
                composeState = composeState.copy(
                    isLoading = false,
                    error = "Network error. Please try again."
                )
                feedState = feedState.copy(isPosting = false)
            }
        }
    }

    // Retry loading posts
    fun retryLoadPosts() {
        loadPosts()
    }

    // Clear compose error
    fun clearComposeError() {
        composeState = composeState.copy(error = null)
    }

//    // Clear feed error
//    fun clearFeedError() {
//        feedState = feedState.copy(error = null)
//    }
}