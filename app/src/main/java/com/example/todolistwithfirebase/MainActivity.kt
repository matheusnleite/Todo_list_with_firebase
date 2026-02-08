package com.example.todolistwithfirebase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todolistwithfirebase.data.repository.AuthRepositoryImpl
import com.example.todolistwithfirebase.data.repository.TaskRepositoryImpl
import com.example.todolistwithfirebase.presentation.auth.AuthViewModel
import com.example.todolistwithfirebase.presentation.navigation.NavGraph
import com.example.todolistwithfirebase.presentation.navigation.Routes
import com.example.todolistwithfirebase.presentation.tasks.TaskViewModel
import com.example.todolistwithfirebase.ui.theme.TodoListWithFirebaseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListWithFirebaseTheme {
                // Inicializar Firebase
                val firebaseAuth = FirebaseAuth.getInstance()
                val firebaseFirestore = FirebaseFirestore.getInstance()

                // Criar repositórios
                val authRepository = AuthRepositoryImpl(firebaseAuth)
                val taskRepository = TaskRepositoryImpl(firebaseFirestore)

                // Criar ViewModels
                val authViewModel = remember { AuthViewModel(authRepository) }
                val taskViewModel = remember { TaskViewModel(taskRepository) }

                // NavController
                val navController = rememberNavController()

                // Verificar usuário logado
                val currentUser by authRepository.getCurrentUser().collectAsState(initial = null)
                val userId = currentUser?.id ?: ""
                Log.d("MainActivity", "Current userId: $userId, user: $currentUser")
                val startDestination = if (currentUser != null) {
                    Routes.TaskList.route
                } else {
                    Routes.Login.route
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        authViewModel = authViewModel,
                        taskViewModel = taskViewModel,
                        userId = userId
                    )
                }
            }
        }
    }
}