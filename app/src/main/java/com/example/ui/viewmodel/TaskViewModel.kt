package com.example.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.data.models.*
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondarySalmon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val currentUser: User = User("1", "Alex Wilson", fame = 1500, followers = 45000),
    val financials: Financials = Financials(balance = 12500.0, monthlyRevenue = 4500.0, monthlyExpenses = 2800.0),
    val projects: List<Project> = emptyList(),
    val dailyTasks: List<Task> = emptyList(),
    val selectedTask: Task? = null
)

class TaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val projects = listOf(
            Project("1", "Debut Album", "Recording & Production", "Music", PrimaryPurple, progress = 0.45f),
            Project("2", "Summer Tour", "Booking & Logistics", "Career", SecondarySalmon, progress = 0.2f),
            Project("3", "Merch Line", "Design & Sales", "Business", Color(0xFFFAB1A0), progress = 0.8f),
            Project("4", "Social Growth", "Marketing Campaign", "Fame", Color(0xFF55EFC4), progress = 0.6f)
        )

        val dailyTasks = listOf(
            Task(
                id = "101",
                title = "Studio Session",
                category = "Production",
                status = TaskStatus.TODO,
                cost = 500.0,
                potentialRevenue = 0.0,
                subTasks = listOf(
                    SubTask("s1", "Vocal Recording", true),
                    SubTask("s2", "Mixing", false),
                    SubTask("s3", "Mastering", false)
                )
            ),
            Task(
                id = "102",
                title = "Instagram Live",
                category = "Marketing",
                status = TaskStatus.TODO,
                cost = 0.0,
                potentialRevenue = 0.0,
                subTasks = listOf(
                    SubTask("s4", "Set up lighting", false),
                    SubTask("s5", "Prepare setlist", false)
                )
            ),
            Task(
                id = "103",
                title = "Review Contract",
                category = "Legal",
                status = TaskStatus.TODO,
                cost = 200.0
            )
        )

        _uiState.value = _uiState.value.copy(
            projects = projects,
            dailyTasks = dailyTasks
        )
    }

    fun selectTask(taskId: String) {
        val task = _uiState.value.dailyTasks.find { it.id == taskId }
            ?: _uiState.value.projects.flatMap { it.tasks }.find { it.id == taskId }

        _uiState.update { it.copy(selectedTask = task) }
    }

    fun toggleSubTask(taskId: String, subTaskId: String) {
        _uiState.update { currentState ->
            val updatedDailyTasks = currentState.dailyTasks.map { task ->
                if (task.id == taskId) {
                    val updatedSubTasks = task.subTasks.map { subTask ->
                        if (subTask.id == subTaskId) {
                            subTask.copy(isCompleted = !subTask.isCompleted)
                        } else subTask
                    }
                    task.copy(subTasks = updatedSubTasks)
                } else task
            }

            val updatedSelectedTask = if (currentState.selectedTask?.id == taskId) {
                updatedDailyTasks.find { it.id == taskId }
            } else currentState.selectedTask

            currentState.copy(
                dailyTasks = updatedDailyTasks,
                selectedTask = updatedSelectedTask
            )
        }
    }

    fun completeTask(taskId: String) {
        _uiState.update { currentState ->
            val task = currentState.dailyTasks.find { it.id == taskId } ?: return@update currentState

            val newBalance = currentState.financials.balance - task.cost + task.potentialRevenue
            val newFollowers = currentState.currentUser.followers + (if (task.category == "Marketing") 150 else 0)

            val updatedDailyTasks = currentState.dailyTasks.filter { it.id != taskId }

            currentState.copy(
                financials = currentState.financials.copy(balance = newBalance),
                currentUser = currentState.currentUser.copy(followers = newFollowers),
                dailyTasks = updatedDailyTasks,
                selectedTask = null
            )
        }
    }
}
