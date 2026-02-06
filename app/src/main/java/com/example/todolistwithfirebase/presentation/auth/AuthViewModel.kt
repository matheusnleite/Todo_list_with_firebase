package com.example.todolistwithfirebase.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistwithfirebase.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Claude - início
 * Prompt: Criar ViewModel para gerenciar autenticação com Firebase
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }
            result.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(email, password)
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }
            result.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Sign up failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
/**
 * Claude - final
 */
