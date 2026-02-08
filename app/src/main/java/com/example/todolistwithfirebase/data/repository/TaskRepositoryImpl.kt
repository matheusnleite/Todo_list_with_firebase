package com.example.todolistwithfirebase.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.todolistwithfirebase.domain.model.Task
import com.example.todolistwithfirebase.domain.repository.TaskRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Claude - Estrutura de sincronização Firestore
 * Prompt: Implementar TaskRepository com Firestore, incluindo snapshot listener com callbackFlow para sincronização em tempo real
 * Implementação com callbackFlow + snapshot listener
 */
class TaskRepositoryImpl(
    private val firebaseFirestore: FirebaseFirestore
) : TaskRepository {

    override fun getTasks(userId: String): Flow<List<Task>> = callbackFlow {
        Log.d("TaskRepository", "getTasks called with userId: $userId")
        val listener = firebaseFirestore
            .collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener(com.google.firebase.firestore.MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e("TaskRepository", "Firestore error: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }
                Log.d("TaskRepository", "Received snapshot with ${snapshot?.documents?.size ?: 0} documents")
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Task(
                            id = doc.getString("id") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            userId = doc.getString("userId") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            val taskMap = mapOf(
                "id" to task.id,
                "title" to task.title,
                "description" to task.description,
                "isCompleted" to task.isCompleted,
                "userId" to task.userId,
                "timestamp" to task.timestamp
            )
            Log.d("TaskRepository", "Adding task with userId: ${task.userId}")
            firebaseFirestore.collection("tasks").document(task.id).set(taskMap).await()
            Log.d("TaskRepository", "Task added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error adding task: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val updateMap = mapOf<String, Any>(
                "title" to task.title,
                "description" to task.description,
                "isCompleted" to task.isCompleted,
                "timestamp" to task.timestamp
            )
            Log.d("TaskRepository", "Updating task ${task.id}")
            firebaseFirestore.collection("tasks").document(task.id).update(updateMap).await()
            Log.d("TaskRepository", "Task updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String, userId: String): Result<Unit> {
        return try {
            Log.d("TaskRepository", "Deleting task $taskId for user $userId")
            firebaseFirestore.collection("tasks").document(taskId).delete().await()
            Log.d("TaskRepository", "Task deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting task: ${e.message}", e)
            Result.failure(e)
        }
    }
}
