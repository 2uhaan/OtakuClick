package com.ruhaan.otakuclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ruhaan.otakuclick.data.models.ClubPost
import com.ruhaan.otakuclick.data.models.CreatePostRequest
import com.ruhaan.otakuclick.data.notifications.ClubNotificationManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class ClubRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")

    private val notificationManager = ClubNotificationManager() // Add this


    // Create a new post
    suspend fun createPost(request: CreatePostRequest): Result<ClubPost> {
        return try {
            val postId = postsCollection.document().id
            val post = ClubPost(
                id = postId,
                userId = request.userId,
                username = request.username,
                text = request.text.trim(),
                timestamp = System.currentTimeMillis()
            )

            postsCollection.document(postId).set(post).await()

            // Trigger notifications asynchronously (don't wait for it)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    notificationManager.sendNewPostNotification(post)
                    println("DEBUG: Notification sent for post by ${post.username}")
                } catch (e: Exception) {
                    println("DEBUG: Failed to send notification: ${e.message}")
                }
            }

            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get posts as real-time flow
    fun getPostsFlow(): Flow<List<ClubPost>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50) // Limit to latest 50 posts
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubPost::class.java)
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    // Get posts once (for initial load)
    suspend fun getPosts(): Result<List<ClubPost>> {
        return try {
            val snapshot = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ClubPost::class.java)
            }

            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete a post (for the post owner)
//    suspend fun deletePost(postId: String, userId: String): Result<Unit> {
//        return try {
//            // First verify the post belongs to the user
//            val postDoc = postsCollection.document(postId).get().await()
//            val post = postDoc.toObject(ClubPost::class.java)
//
//            if (post?.userId == userId) {
//                postsCollection.document(postId).delete().await()
//                Result.success(Unit)
//            } else {
//                Result.failure(Exception("Unauthorized to delete this post"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

}