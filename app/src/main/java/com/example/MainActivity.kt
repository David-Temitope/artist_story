package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DailyTaskScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(taskViewModel: TaskViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by taskViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                user = uiState.currentUser,
                financials = uiState.financials,
                projects = uiState.projects,
                dailyTasks = uiState.dailyTasks,
                onProjectClick = { taskId ->
                    taskViewModel.selectTask(taskId)
                    navController.navigate("task_detail")
                },
                onDailyTaskClick = { navController.navigate("daily_task") }
            )
        }
        composable("daily_task") {
            DailyTaskScreen(
                tasks = uiState.dailyTasks,
                onBackClick = { navController.popBackStack() },
                onTaskClick = { taskId ->
                    taskViewModel.selectTask(taskId)
                    navController.navigate("task_detail")
                }
            )
        }
        composable("task_detail") {
            TaskDetailScreen(
                task = uiState.selectedTask,
                onBackClick = { navController.popBackStack() },
                onSubTaskToggle = { taskId, subTaskId -> taskViewModel.toggleSubTask(taskId, subTaskId) },
                onCompleteTask = { taskId ->
                    taskViewModel.completeTask(taskId)
                    navController.popBackStack()
                }
            )
        }
    }
}
