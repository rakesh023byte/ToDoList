package com.example.todolist.model

data class User(
    val name: String = "",  // Name of the user
    val email: String = "", // Email of the user
    val uid: String = ""    // Firebase UID (unique identifier)
)
