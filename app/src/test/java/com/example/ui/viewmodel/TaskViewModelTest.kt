package com.example.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TaskViewModelTest {

    @Test
    fun `initial state has user and data`() {
        val viewModel = TaskViewModel()
        val state = viewModel.uiState.value

        assertEquals("Alex Wilson", state.currentUser.name)
        assertEquals(4, state.projects.size)
        assertEquals(3, state.dailyTasks.size)
    }

    @Test
    fun `selecting task updates state`() {
        val viewModel = TaskViewModel()
        viewModel.selectTask("101")

        val state = viewModel.uiState.value
        assertNotNull(state.selectedTask)
        assertEquals("Studio Session", state.selectedTask?.title)
    }

    @Test
    fun `toggling subtask updates progress`() {
        val viewModel = TaskViewModel()
        val taskId = "101"
        val subTaskId = "s2"

        // Initial progress for Studio Session: 1 out of 3 subtasks are completed (33.3%)
        viewModel.selectTask(taskId)
        assertEquals(0.33f, viewModel.uiState.value.selectedTask?.progress ?: 0f, 0.01f)

        viewModel.toggleSubTask(taskId, subTaskId)

        // After toggling, 2 out of 3 are completed (66.6%)
        assertEquals(0.66f, viewModel.uiState.value.selectedTask?.progress ?: 0f, 0.01f)
    }
}
