package com.example.todolistwithfirebase.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistwithfirebase.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    // Instância direta do Auth para pegar o estado da sessão
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Flow que a TaskListScreen vai observar
    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // Ouve alterações na autenticação (login/logout) automaticamente
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
                // O listener no init já vai atualizar o _currentUser automaticamente
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
            // Esta chamada agora invoca a função de suspensão que limpa o cache.
            // O código parece o mesmo, mas o comportamento subjacente mudou.
            authRepository.logout()

            // Esta linha pode ser redundante se o seu AuthStateListener já
            // estiver tratando a UI, mas não causa problemas.
            _authState.value = AuthState.Idle
            // O listener no init vai detectar o logout e setar _currentUser para null
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

/**
 * Claude - final
 */