package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        firestore.collection("tasks")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val tasks = snapshot.toObjects(Task::class.java)
                    _taskList.value = tasks
                }
            }
    }

    fun addTask(task: Task) {
        firestore.collection("tasks").add(task)
    }
}
