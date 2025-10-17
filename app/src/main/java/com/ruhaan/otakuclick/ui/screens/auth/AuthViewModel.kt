package com.ruhaan.otakuclick.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.otakuclick.data.auth.FirebaseAuthManager
import com.ruhaan.otakuclick.data.models.AuthResult
import com.ruhaan.otakuclick.data.models.LoginFormState
import com.ruhaan.otakuclick.data.models.SignUpFormState
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class AuthViewModel(
    private val authManager: FirebaseAuthManager = FirebaseAuthManager()
) : ViewModel() {

    // Authentication state
    var authState by mutableStateOf<AuthResult>(AuthResult.None)
        private set

    // Form states
    var loginFormState by mutableStateOf(LoginFormState())
        private set

    var signUpFormState by mutableStateOf(SignUpFormState())
        private set

    // Check if user is already logged in
    init {
        loadCurrentUser()
    }

    // Update the loadCurrentUser function in AuthViewModel:
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                println("DEBUG: Starting to load current user...")
                // Set loading state
                authState = AuthResult.Loading

                // Wait a bit for Firebase Auth to initialize
                delay(500)

                if (authManager.isUserLoggedIn()) {
                    val user = authManager.getCurrentUser()
                    println("DEBUG: Retrieved user: ${user?.username} (${user?.uid})")

                    if (user != null) {
                        authState = AuthResult.Success(user)
                        // Register FCM token
                        registerFCMToken(user.uid)
                    } else {
                        println("DEBUG: User is null, setting state to None")
                        authState = AuthResult.None
                    }
                } else {
                    println("DEBUG: User not logged in, setting state to None")
                    authState = AuthResult.None
                }
            } catch (e: Exception) {
                println("DEBUG: Error loading user: ${e.message}")
                authState = AuthResult.None
            }
        }
    }

//    // Add this new function:
//    private fun loadCurrentUser() {
//        if (authManager.isUserLoggedIn()) {
//            authState = AuthResult.Loading
//
//            viewModelScope.launch {
//                try {
//                    val user = authManager.getCurrentUser()
//                    authState = (if (user != null) {
//                        AuthResult.Success(user)
//
//                        // Register FCM token for existing users
//                        registerFCMToken(user.uid)
//
//                    } else {
//                        AuthResult.None
//                    }) as AuthResult
//                } catch (_: Exception) {
//                    // If we can't get user data, sign them out
//                    authManager.signOut()
//                    authState = AuthResult.None
//                }
//            }
//        }
//    }


//    init {
//        if (authManager.isUserLoggedIn()) {
//            authManager.getCurrentUser()?.let { user ->
//                authState = AuthResult.Success(user)
//            }
//        }
//    }

    // Login functions
    fun updateLoginEmail(email: String) {
        loginFormState = loginFormState.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updateLoginPassword(password: String) {
        loginFormState = loginFormState.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    fun login() {
        // Validate form
        val emailError = validateEmail(loginFormState.email)
        val passwordError = validatePassword(loginFormState.password)

        loginFormState = loginFormState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        if (emailError != null || passwordError != null) return

        // Start login process
        loginFormState = loginFormState.copy(isLoading = true)
        authState = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = authManager.signIn(
                    email = loginFormState.email.trim(),
                    password = loginFormState.password
                )

                result.fold(
                    onSuccess = { user ->
                        authState = AuthResult.Success(user)
                        loginFormState = loginFormState.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        authState = AuthResult.Error(getAuthErrorMessage(exception))
                        loginFormState = loginFormState.copy(isLoading = false)
                    }
                )
            } catch (_: Exception) {
                authState = AuthResult.Error("Network error. Please try again.")
                loginFormState = loginFormState.copy(isLoading = false)
            }
        }

        //Notification after Login
        viewModelScope.launch {
            try {
                val result = authManager.signIn(
                    email = loginFormState.email.trim(),
                    password = loginFormState.password
                )

                result.fold(
                    onSuccess = { user ->
                        authState = AuthResult.Success(user)
                        loginFormState = loginFormState.copy(isLoading = false)

                        // Register FCM token after successful login
                        registerFCMToken(user.uid)
                    },
                    onFailure = { exception ->
                        authState = AuthResult.Error(getAuthErrorMessage(exception))
                        loginFormState = loginFormState.copy(isLoading = false)
                    }
                )
            } catch (_: Exception) {
                authState = AuthResult.Error("Network error. Please try again.")
                loginFormState = loginFormState.copy(isLoading = false)
            }
        }


    }

    // Sign up functions
    fun updateSignUpUsername(username: String) {
        signUpFormState = signUpFormState.copy(
            username = username,
            usernameError = validateUsername(username)
        )
    }

    fun updateSignUpEmail(email: String) {
        signUpFormState = signUpFormState.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updateSignUpPassword(password: String) {
        signUpFormState = signUpFormState.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    fun updateSignUpConfirmPassword(confirmPassword: String) {
        signUpFormState = signUpFormState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(
                signUpFormState.password,
                confirmPassword
            )
        )
    }

    fun signUp() {
        // Validate all fields
        val usernameError = validateUsername(signUpFormState.username)
        val emailError = validateEmail(signUpFormState.email)
        val passwordError = validatePassword(signUpFormState.password)
        val confirmPasswordError = validateConfirmPassword(
            signUpFormState.password,
            signUpFormState.confirmPassword
        )

        signUpFormState = signUpFormState.copy(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        if (usernameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null) return

        // Start sign up process
        signUpFormState = signUpFormState.copy(isLoading = true)
        authState = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = authManager.signUp(
                    email = signUpFormState.email.trim(),
                    password = signUpFormState.password,
                    username = signUpFormState.username.trim()
                )

                result.fold(
                    onSuccess = { user ->
                        authState = AuthResult.Success(user)
                        signUpFormState = signUpFormState.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        authState = AuthResult.Error(getAuthErrorMessage(exception))
                        signUpFormState = signUpFormState.copy(isLoading = false)
                    }
                )
            } catch (_: Exception) {
                authState = AuthResult.Error("Network error. Please try again.")
                signUpFormState = signUpFormState.copy(isLoading = false)
            }
        }

        viewModelScope.launch {
            try {
                val result = authManager.signUp(
                    email = signUpFormState.email.trim(),
                    password = signUpFormState.password,
                    username = signUpFormState.username.trim()
                )

                result.fold(
                    onSuccess = { user ->
                        authState = AuthResult.Success(user)
                        signUpFormState = signUpFormState.copy(isLoading = false)

                        // Register FCM token after successful signup
                        registerFCMToken(user.uid)
                    },
                    onFailure = { exception ->
                        authState = AuthResult.Error(getAuthErrorMessage(exception))
                        signUpFormState = signUpFormState.copy(isLoading = false)
                    }
                )
            } catch (_: Exception) {
                authState = AuthResult.Error("Network error. Please try again.")
                signUpFormState = signUpFormState.copy(isLoading = false)
            }
        }

    }

    // Logout
    fun logout() {
        authManager.signOut()
        authState = AuthResult.None
        resetForms()
    }

    // Reset error state
    fun clearError() {
        if (authState is AuthResult.Error) {
            authState = AuthResult.None
        }
    }

    // Reset forms
    private fun resetForms() {
        loginFormState = LoginFormState()
        signUpFormState = SignUpFormState()
    }

    // Validation functions
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Username is required"
            username.length < 3 -> "Username must be at least 3 characters"
            username.length > 20 -> "Username must be less than 20 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscore"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords don't match"
            else -> null
        }
    }

    // Convert Firebase errors to user-friendly messages
    private fun getAuthErrorMessage(exception: Throwable): String {
        return when (exception.message) {
            "The email address is already in use by another account." -> "Email already exists. Try logging in instead."
            "The password is invalid or the user does not have a password." -> "Invalid email or password"
            "There is no user record corresponding to this identifier." -> "No account found with this email"
            "The email address is badly formatted." -> "Invalid email format"
            "The given password is invalid." -> "Password must be at least 6 characters"
            else -> exception.message ?: "Authentication failed. Please try again."
        }
    }

    // Add this new function for FCM token registration:
    private fun registerFCMToken(userId: String) {
        viewModelScope.launch {
            try {
                // Small delay to ensure user is fully authenticated
                delay(1000)

                val result = authManager.registerFCMToken(userId)
                result.fold(
                    onSuccess = { token ->
                        println("DEBUG: FCM token registered: ${token.take(20)}...")
                    },
                    onFailure = { exception ->
                        println("DEBUG: FCM token registration failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: FCM token registration error: ${e.message}")
            }
        }
    }

}

