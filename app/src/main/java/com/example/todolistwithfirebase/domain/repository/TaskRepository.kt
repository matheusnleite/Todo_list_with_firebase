package com.example.todolistwithfirebase.domain.repository

import com.example.todolistwithfirebase.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define operações de tarefas
 */
interface TaskRepository {
    /**
     * Obtém todas as tarefas do usuário
     * @param userId ID do usuário proprietário das tarefas
     */
    fun getTasks(userId: String): Flow<List<Task>>

    /**
     * Adiciona uma nova tarefa
     * @param task Tarefa a ser adicionada
     */
    suspend fun addTask(task: Task): Result<Unit>

    /**
     * Atualiza uma tarefa existente
     * @param task Tarefa com dados atualizados
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Deleta uma tarefa
     * @param taskId ID da tarefa a deletar
     * @param userId ID do usuário proprietário
     */
    suspend fun deleteTask(taskId: String, userId: String): Result<Unit>
}
