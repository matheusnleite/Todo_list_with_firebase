package com.example.todolistwithfirebase.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todolistwithfirebase.presentation.auth.AuthViewModel
import com.example.todolistwithfirebase.presentation.auth.LoginScreen
import com.example.todolistwithfirebase.presentation.auth.SignUpScreen
import com.example.todolistwithfirebase.presentation.tasks.TaskListScreen
import com.example.todolistwithfirebase.presentation.tasks.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    userId: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.SignUp.route) {
            SignUpScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.TaskList.route) {
            TaskListScreen(
                taskViewModel = taskViewModel,
                authViewModel = authViewModel,
                navController = navController,
                userId = userId
            )
        }
    }
}
