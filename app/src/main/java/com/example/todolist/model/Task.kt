package com.example.todolist.model

data class Task(
    var id: String = "",  // ✅ Ensure we have an ID field
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    var isCompleted: Boolean = false,
    val userId: String = ""  // Ensure every task is linked to a user
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time,
            "isCompleted" to isCompleted,
            "userId" to userId
        )
    }
}
