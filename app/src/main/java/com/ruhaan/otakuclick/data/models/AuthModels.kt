package com.ruhaan.otakuclick.data.models

// User authentication model
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val joinDate: Long = System.currentTimeMillis(),
    val fcmToken: String = ""  // Add FCM token field
) {
    // Computed property for display name
    val displayName: String
        get() = username.ifBlank { "User${uid.take(6)}" }
}

// Authentication result states
sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object None : AuthResult()
}

// Form validation states
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

data class SignUpFormState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)
