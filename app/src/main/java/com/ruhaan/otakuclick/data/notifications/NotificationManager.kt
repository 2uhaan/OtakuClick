package com.ruhaan.otakuclick.data.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.ruhaan.otakuclick.data.models.ClubPost
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubNotificationManager @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val messaging = FirebaseMessaging.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Send notification to all users when new post is created
    suspend fun sendNewPostNotification(post: ClubPost): Result<Unit> {
        return try {
            // Get all FCM tokens from users
            val tokensResult = getAllUserTokens()
            if (tokensResult.isFailure) {
                return Result.failure(tokensResult.exceptionOrNull() ?: Exception("Failed to get tokens"))
            }

            val tokens = tokensResult.getOrNull() ?: emptyList()
            if (tokens.isEmpty()) {
                return Result.success(Unit) // No users to notify
            }

            // Create notification payload
            val notificationData = mapOf(
                "title" to "🎭 Club",
                "body" to "${post.username} posted",
                "username" to post.username,
                "postText" to post.text,
                "postId" to post.id,
                "type" to "new_post"
            )

            // Send notification to each token
            tokens.forEach { token ->
                sendNotificationToToken(token, notificationData)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all user FCM tokens
    private suspend fun getAllUserTokens(): Result<List<String>> {
        return try {
            val snapshot = firestore.collection("users")
                .whereNotEqualTo("fcmToken", "")
                .get()
                .await()

            val tokens = snapshot.documents.mapNotNull { doc ->
                val token = doc.getString("fcmToken")
                if (token?.isNotBlank() == true) token else null
            }

            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Send notification to specific token
    private suspend fun sendNotificationToToken(token: String, data: Map<String, String>) {
        try {
            // Since we can't send from client directly, we'll use a simpler approach
            // For now, we'll trigger local notifications when new posts are detected
            println("DEBUG: Would send notification to token: ${token.take(20)}... with data: $data")
        } catch (e: Exception) {
            println("DEBUG: Failed to send notification: ${e.message}")
        }
    }
}