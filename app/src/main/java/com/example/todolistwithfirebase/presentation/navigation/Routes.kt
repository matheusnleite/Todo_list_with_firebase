package com.example.todolistwithfirebase.presentation.navigation

/**
 * Sealed class que define as rotas de navegação da aplicação
 */
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object SignUp : Routes("signup")
    object TaskList : Routes("tasklist")
}
