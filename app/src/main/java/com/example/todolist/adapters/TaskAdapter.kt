package com.example.todolist.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.AddTaskActivity
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            // Ensure task is valid before binding
            if (tasks.isNotEmpty()) {
                binding.txtTaskTitle.text = task.title
                binding.txtTaskDescription.text = task.description
                binding.txtTaskDate.text = "📅 ${task.date}"
                binding.txtTaskTime.text = "⏰ ${task.time}"

                binding.checkboxComplete.setOnCheckedChangeListener(null)
                binding.checkboxComplete.isChecked = task.isCompleted
                binding.checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                    task.isCompleted = isChecked
                    onTaskCompleted(task)
                }

                // Edit Task
                binding.btnEdit.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, AddTaskActivity::class.java).apply {
                        putExtra("TASK_ID", task.id)
                        putExtra("TASK_TITLE", task.title)
                        putExtra("TASK_DESC", task.description)
                        putExtra("TASK_DATE", task.date)
                        putExtra("TASK_TIME", task.time)
                    }
                    context.startActivity(intent)
                }

                // Delete Task
                binding.btnDelete.setOnClickListener {
                    onTaskDeleted(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Check if position is valid before binding
        if (position >= 0 && position < tasks.size) {
            holder.bind(tasks[position])
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
