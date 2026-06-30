package com.github.fziraki.daykit.internal.todo

import com.github.fziraki.daykit.model.TodoItem
import com.github.fziraki.daykit.providers.TodoProvider

internal class StubTodoProvider : TodoProvider {
    override suspend fun isAuthenticated(): Boolean = true

    override suspend fun getPendingTasks(): List<TodoItem> = listOf(
        TodoItem(
            id = "1",
            title = "Buy groceries",
            isCompleted = false,
            dueDate = 123546468
        ),
        TodoItem(
            id = "2",
            title = "Call the bank",
            isCompleted = false,
            dueDate = 2465465464
        )
    )

    override suspend fun completeTask(id: String): Boolean = true
}