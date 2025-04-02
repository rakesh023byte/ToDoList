package com.example.todolist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.AddTaskActivity
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.databinding.FragmentHomeBinding
import com.example.todolist.model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Home"

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up RecyclerView with TaskAdapter
        taskAdapter = TaskAdapter(taskList, ::markTaskAsCompleted, ::deleteTask)
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        // Floating Action Button (FAB) to add a new task
        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(requireActivity(), AddTaskActivity::class.java))
        }

        // Start listening for task updates
        listenForTaskUpdates()
    }

    // Listen for task updates from Firestore
    private fun listenForTaskUpdates() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed to load tasks", Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }

                snapshots?.let {
                    taskList.clear() // Clear the current task list
                    for (document in it) {
                        val task = document.toObject(Task::class.java)
                        task.id = document.id  // Ensure ID is set correctly for deletion or updates
                        taskList.add(task)
                    }
                    sortAndUpdateTasks()
                }
            }
    }

    // Sort tasks by completion status and update the RecyclerView
    private fun sortAndUpdateTasks() {
        taskList.sortBy { it.isCompleted }
        taskAdapter.notifyDataSetChanged() // Notify adapter for data update
    }

    // Mark task as completed and update in Firestore
    private fun markTaskAsCompleted(task: Task) {
        if (task.id.isEmpty()) {
            Snackbar.make(binding.root, "Error: Task ID is missing", Snackbar.LENGTH_SHORT).show()
            return
        }

        val taskRef = firestore.collection("tasks").document(task.id)
        taskRef.update("isCompleted", task.isCompleted)
            .addOnSuccessListener {
                Snackbar.make(
                    binding.root,
                    if (task.isCompleted) "Task completed!" else "Task marked as pending!",
                    Snackbar.LENGTH_SHORT
                ).show()
                sortAndUpdateTasks() // Refresh task list after update
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, "Failed to update task", Snackbar.LENGTH_SHORT).show()
            }
    }

    // Delete task from Firestore and update UI
    private fun deleteTask(task: Task) {
        if (task.id.isEmpty()) {
            Snackbar.make(binding.root, "Error: Task ID is missing", Snackbar.LENGTH_SHORT).show()
            return
        }

        firestore.collection("tasks").document(task.id).delete()
            .addOnSuccessListener {
                taskList.remove(task) // Remove task from the list
                taskAdapter.notifyDataSetChanged() // Update RecyclerView
                Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, "Failed to delete task", Snackbar.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
