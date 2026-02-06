package com.example.todolistwithfirebase.domain.repository

import com.example.todolistwithfirebase.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define operações de autenticação
 */
interface AuthRepository {
    /**
     * Realiza login do usuário com email e senha
     * @param email Email do usuário
     * @param password Senha do usuário
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Realiza cadastro de novo usuário
     * @param email Email do novo usuário
     * @param password Senha do novo usuário
     */
    suspend fun signUp(email: String, password: String): Result<User>

    /**
     * Realiza logout do usuário
     */
    suspend fun logout(): Result<Unit>

    /**
     * Obtém o usuário atualmente logado
     */
    fun getCurrentUser(): Flow<User?>
}
