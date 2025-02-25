package com.example.tasklist.model


data class User(
    val name: String = "",
    val imagen: String = "",
    val tasks: Map<String, Task> = emptyMap()
)