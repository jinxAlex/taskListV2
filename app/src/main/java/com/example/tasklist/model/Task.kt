package com.example.tasklist.model

import java.io.Serializable

data class Task(
    val nombre: String = "",
    val tiempo: Int = 0,
    var terminado: Boolean = false,
    val categoria: String = "",
    val altaPrioridad: Boolean = false
): Serializable