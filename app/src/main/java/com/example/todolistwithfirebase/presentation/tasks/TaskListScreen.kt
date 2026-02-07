package com.example.todolistwithfirebase.presentation.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolistwithfirebase.domain.model.Task
import com.example.todolistwithfirebase.presentation.auth.AuthViewModel
import com.example.todolistwithfirebase.presentation.navigation.Routes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.UUID

/**
 * Claude - início
 * Prompt: Criar tela de lista de tarefas com Scaffold, TopAppBar, FAB, LazyColumn e TaskItem
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    userId: String,
    modifier: Modifier = Modifier
) {
    val taskState by taskViewModel.taskState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Carregar tarefas ao abrir a tela ou quando o userId muda
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            taskViewModel.loadTasks(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.TaskList.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = taskState) {
            is TaskState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TaskState.Success -> {
                if (state.tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma tarefa ainda.\nClique no + para criar uma!",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(8.dp)
                    ) {
                        items(state.tasks) { task ->
                            TaskItem(
                                task = task,
                                onToggle = { taskViewModel.updateTask(it) },
                                onEdit = {
                                    selectedTask = it
                                    showEditDialog = true
                                },
                                onDelete = {
                                    selectedTask = it
                                    showDeleteConfirmation = true
                                }
                            )
                        }
                    }
                }
            }

            is TaskState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Erro: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Dialog para adicionar tarefa
    if (showAddDialog) {
        AddTaskDialog(
            onConfirm = { title, description ->
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    userId = userId,
                    timestamp = System.currentTimeMillis()
                )
                taskViewModel.addTask(newTask)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // Dialog para editar tarefa
    if (showEditDialog && selectedTask != null) {
        EditTaskDialog(
            task = selectedTask!!,
            onConfirm = { title, description ->
                val updatedTask = selectedTask!!.copy(
                    title = title,
                    description = description
                )
                taskViewModel.updateTask(updatedTask)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // Dialog de confirmação de delete
    if (showDeleteConfirmation && selectedTask != null) {
        DeleteConfirmationDialog(
            task = selectedTask!!,
            onConfirm = {
                taskViewModel.deleteTask(selectedTask!!.id, userId)
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

/**
 * Composable que exibe um item de tarefa
 */
@Composable
fun TaskItem(
    task: Task,
    onToggle: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox e texto
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle(task.copy(isCompleted = it)) }
                )

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Botões de ação
            Row {
                IconButton(onClick = { onEdit(task) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(task) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Dialog para adicionar nova tarefa
 */
@Composable
fun AddTaskDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Tarefa") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onConfirm(title, description)
                    }
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Dialog para editar tarefa existente
 */
@Composable
fun EditTaskDialog(
    task: Task,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarefa") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onConfirm(title, description)
                    }
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Dialog de confirmação para deletar tarefa
 */
@Composable
fun DeleteConfirmationDialog(
    task: Task,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deletar Tarefa") },
        text = { Text("Tem certeza que deseja deletar '${task.title}'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Deletar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Claude - final
 */
