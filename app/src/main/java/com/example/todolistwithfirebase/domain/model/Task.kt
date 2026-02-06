package com.example.todolistwithfirebase.domain.model

/**
 * Data class que representa uma tarefa
 * @param id ID único da tarefa no Firestore
 * @param title Título da tarefa
 * @param description Descrição detalhada da tarefa
 * @param isCompleted Se a tarefa foi concluída
 * @param userId ID do usuário proprietário da tarefa
 * @param timestamp Data de criação da tarefa
 */
data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val userId: String = "",
    val timestamp: Long = 0L
)
