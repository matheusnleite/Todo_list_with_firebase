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
 * Claude - Padrão flatMapLatest + stateIn
 * Prompt: Criar ViewModel para gerenciar tarefas com Firestore, mantendo listener ativo e sincronizando em tempo real
 *
 * ViewModel responsável pelo gerenciamento de tarefas
 * Utiliza flatMapLatest para automaticamente carregar tarefas quando userId muda
 * Mantém listener do Firestore ativo enquanto ViewModel estiver vivo (SharingStarted.Lazily)
 */
class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    /**
     * StateFlow que emite a lista de tarefas do usuário atual
     * Atualiza automaticamente quando dados mudam no Firestore
     */
    val taskState: StateFlow<TaskState> = _userId
        .flatMapLatest { userId ->
            if (userId != null) {
                taskRepository.getTasks(userId)
                    .map { tasks -> TaskState.Success(tasks) as TaskState }
                    .catch { error ->
                        emit(TaskState.Error(error.message ?: "Error loading tasks") as TaskState)
                    }
            } else {
                flowOf(TaskState.Success(emptyList()) as TaskState)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TaskState.Loading
        )

    /**
     * Carrega as tarefas do usuário especificado
     * Muda o userId, que dispara automaticamente a observação de tarefas via flatMapLatest
     * @param userId ID do usuário, ou null para limpar lista
     */
    fun loadTasks(userId: String?) {
        Log.d("TaskViewModel", "loadTasks called with userId: $userId")
        _userId.value = userId
    }

    /**
     * Adiciona uma nova tarefa ao Firestore
     * @param task Tarefa a ser adicionada
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.addTask(task)
            if (result.isFailure) {
                // Task update will be reflected automatically via stateIn
            }
        }
    }

    /**
     * Atualiza uma tarefa existente no Firestore
     * @param task Tarefa com dados atualizados
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.updateTask(task)
            if (result.isFailure) {
                // Task update will be reflected automatically via stateIn
            }
        }
    }

    /**
     * Deleta uma tarefa do Firestore
     * @param taskId ID da tarefa a deletar
     * @param userId ID do usuário proprietário
     */
    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            val result = taskRepository.deleteTask(taskId, userId)
            if (result.isFailure) {
                // Task deletion will be reflected automatically via stateIn
            }
        }
    }

    /**
     * Marca/desmarca uma tarefa como concluída
     * @param task Tarefa a ser toggled
     */
    fun toggleTaskCompletion(task: Task) {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }
}
