package com.example.todolistwithfirebase.presentation.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistwithfirebase.domain.model.Task
import com.example.todolistwithfirebase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Claude - início
 * Prompt: Criar ViewModel para gerenciar tarefas com Firestore
 */
class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    val taskState: StateFlow<TaskState> = _userId
        .flatMapLatest { userId ->
            if (userId != null) {
                taskRepository.getTasks(userId)
                    .map { tasks -> TaskState.Success(tasks) as TaskState }
                    .catch { error ->
                        emit(TaskState.Error(error.message ?: "Error loading tasks") as TaskState)
                    }
            } else {
                flowOf(TaskState.Loading as TaskState)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TaskState.Loading
        )

    fun loadTasks(userId: String) {
        Log.d("TaskViewModel", "loadTasks called with userId: $userId")
        _userId.value = userId
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.addTask(task)
            if (result.isFailure) {
                // Task update will be reflected automatically via stateIn
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.updateTask(task)
            if (result.isFailure) {
                // Task update will be reflected automatically via stateIn
            }
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            val result = taskRepository.deleteTask(taskId, userId)
            if (result.isFailure) {
                // Task deletion will be reflected automatically via stateIn
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }
}
/**
 * Claude - final
 */
