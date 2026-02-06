package com.example.todolistwithfirebase.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.example.todolistwithfirebase.domain.model.User
import com.example.todolistwithfirebase.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

/**
 * Claude - início
 * Prompt: Criar implementação de AuthRepository com Firebase Auth
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(User(id = user.uid, email = user.email ?: ""))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(User(id = user.uid, email = user.email ?: ""))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return kotlinx.coroutines.flow.callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    Log.d("AuthRepository", "User logged in: ${currentUser.uid} (${currentUser.email})")
                    trySend(User(id = currentUser.uid, email = currentUser.email ?: ""))
                } else {
                    Log.d("AuthRepository", "User logged out")
                    trySend(null)
                }
            }
            firebaseAuth.addAuthStateListener(authStateListener)
            awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
        }
    }
}
/**
 * Claude - final
 */
