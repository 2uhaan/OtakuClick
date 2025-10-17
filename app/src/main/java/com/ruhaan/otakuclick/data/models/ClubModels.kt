package com.ruhaan.otakuclick.data.models


// Social post model
data class ClubPost(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

// Post creation model
data class CreatePostRequest(
    val text: String,
    val userId: String,
    val username: String
)

// Club feed state
data class ClubFeedState(
    val posts: List<ClubPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPosting: Boolean = false
)

// Post compose state
data class ComposePostState(
    val text: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val characterCount: Int = 0
) {
    val isValid: Boolean
        get() = text.trim().isNotEmpty() && text.length <= 500

    val remainingCharacters: Int
        get() = 500 - text.length
}
