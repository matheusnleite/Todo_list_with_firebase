package com.example.todolistwithfirebase.presentation.auth

import com.example.todolistwithfirebase.domain.model.User

/**
 * Sealed class que representa os possíveis estados da autenticação
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
