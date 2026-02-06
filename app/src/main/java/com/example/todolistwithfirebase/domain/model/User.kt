package com.example.todolistwithfirebase.domain.model

/**
 * Data class que representa um usuário
 * @param id ID único do usuário (mesmo do Firebase Auth)
 * @param email Email do usuário
 */
data class User(
    val id: String = "",
    val email: String = ""
)
