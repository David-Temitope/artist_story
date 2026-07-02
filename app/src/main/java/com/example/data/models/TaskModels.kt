package com.example.data.models

import androidx.compose.ui.graphics.Color

data class User(
    val id: String,
    val name: String,
    val fame: Int = 0,
    val followers: Int = 0,
    val avatarUrl: String? = null
)

data class Financials(
    val balance: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val transactions: List<Transaction> = emptyList()
)

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val type: TransactionType
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class SubTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: Priority = Priority.MEDIUM,
    val subTasks: List<SubTask> = emptyList(),
    val status: TaskStatus = TaskStatus.TODO,
    val assignedUsers: List<User> = emptyList(),
    val dueDate: String = "",
    val cost: Double = 0.0,
    val potentialRevenue: Double = 0.0
) {
    val progress: Float
        get() = if (subTasks.isEmpty()) {
            if (status == TaskStatus.DONE) 1f else 0f
        } else {
            subTasks.count { it.isCompleted }.toFloat() / subTasks.size
        }
}

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val color: Color,
    val tasks: List<Task> = emptyList(),
    val progress: Float = 0f
)
