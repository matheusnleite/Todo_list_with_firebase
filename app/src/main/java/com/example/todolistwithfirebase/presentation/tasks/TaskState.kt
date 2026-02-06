package com.example.todolistwithfirebase.presentation.tasks

import com.example.todolistwithfirebase.domain.model.Task

/**
 * Sealed class que representa os possíveis estados da lista de tarefas
 */
sealed class TaskState {
    object Loading : TaskState()
    data class Success(val tasks: List<Task>) : TaskState()
    data class Error(val message: String) : TaskState()
}
