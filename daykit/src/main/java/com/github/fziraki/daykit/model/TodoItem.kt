package com.github.fziraki.daykit.model

data class TodoItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val dueDate: Long?
)