package com.ruhaan.otakuclick.data.auth

import com.ruhaan.otakuclick.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.messaging.FirebaseMessaging

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Get current user
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return try {
            // Get user profile from Firestore
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java) ?: User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = "User${firebaseUser.uid.take(4)}" // Fallback username
            )

            user
        } catch (_: Exception) {
            // Fallback to basic user info if Firestore fails
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = "User${firebaseUser.uid.take(4)}" // Fallback username
            )
        }
    }

//    fun getCurrentUser(): User? {
//        val firebaseUser = auth.currentUser
//        return if (firebaseUser != null) {
//            User(
//                uid = firebaseUser.uid,
//                email = firebaseUser.email ?: ""
//            )
//        } else null
//    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Sign up with email and password
    suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return try {
            // Create Firebase Auth account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Create user profile in Firestore
                val user = User(
                    uid = firebaseUser.uid,
                    username = username,
                    email = email
                )

                // Save to Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign in with email and password
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Get user profile from Firestore
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = userDoc.toObject(User::class.java) ?: User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: ""
                )

                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign out
    fun signOut() {
        auth.signOut()
    }

    // Password reset
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Save FCM token to user profile
    suspend fun updateFCMToken(userId: String, token: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get FCM token and save it
    suspend fun registerFCMToken(userId: String): Result<String> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            updateFCMToken(userId, token)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all user FCM tokens (for sending notifications)
    suspend fun getAllUserFCMTokens(): Result<List<String>> {
        return try {
            val snapshot = firestore.collection("users")
                .whereNotEqualTo("fcmToken", "")
                .get()
                .await()

            val tokens = snapshot.documents.mapNotNull { doc ->
                doc.getString("fcmToken")
            }

            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}