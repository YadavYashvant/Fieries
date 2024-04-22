package com.yashvant.fieries.models

data class Task(
    val todo: String,
    val date: String,
    val isDone: Boolean
) {
    constructor(): this("Empty", "22/04/2024", false)
}