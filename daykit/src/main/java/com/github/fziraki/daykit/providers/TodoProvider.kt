package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.TodoItem

interface TodoProvider {
    suspend fun isAuthenticated(): Boolean
    suspend fun getPendingTasks(): List<TodoItem>
    suspend fun completeTask(id: String): Boolean
}