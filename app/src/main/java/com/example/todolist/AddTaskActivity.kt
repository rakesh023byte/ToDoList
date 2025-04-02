package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTitle: EditText
    private lateinit var btnPickDate: ImageButton
    private lateinit var btnPickTime: ImageButton
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var etTaskDescription: EditText
    private lateinit var btnSaveTask: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var taskId: String? = null  // Store Task ID for editing
    private var isEditMode = false  // Track if we're editing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        etTaskTitle = findViewById(R.id.etTaskTitle)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        etTaskDescription = findViewById(R.id.etTaskDescription)
        btnSaveTask = findViewById(R.id.btnSaveTask)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // ✅ Check if this is an Edit Operation
        taskId = intent.getStringExtra("TASK_ID")
        if (taskId != null) {
            isEditMode = true
            loadTaskData()  // Load existing task details
            btnSaveTask.text = "Save Edit"  // Change button text
        }

        // Date Picker
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        // Time Picker
        btnPickTime.setOnClickListener {
            showTimePicker()
        }

        // Save / Update Task
        btnSaveTask.setOnClickListener {
            saveOrUpdateTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            tvSelectedDate.text = "$day/${month + 1}/$year"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            tvSelectedTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun saveOrUpdateTask() {
        val title = etTaskTitle.text.toString().trim()
        val description = etTaskDescription.text.toString().trim()
        val date = tvSelectedDate.text.toString()
        val time = tvSelectedTime.text.toString()

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode) {
            updateTask(title, description, date, time)
        } else {
            saveNewTask(title, description, date, time)
        }
    }

    // ✅ Load Existing Task Data
    private fun loadTaskData() {
        etTaskTitle.setText(intent.getStringExtra("TASK_TITLE"))
        etTaskDescription.setText(intent.getStringExtra("TASK_DESC"))
        tvSelectedDate.text = intent.getStringExtra("TASK_DATE")
        tvSelectedTime.text = intent.getStringExtra("TASK_TIME")
    }

    // ✅ Update Existing Task
    private fun updateTask(title: String, description: String, date: String, time: String) {
        val updatedTask = mapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time
        )

        firestore.collection("tasks").document(taskId!!)
            .update(updatedTask)
            .addOnSuccessListener {
                Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Save New Task
    private fun saveNewTask(title: String, description: String, date: String, time: String) {
        val userId = auth.currentUser?.uid ?: return

        val newTask = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time,
            "userId" to userId
        )

        firestore.collection("tasks")
            .add(newTask)
            .addOnSuccessListener {
                Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show()
            }
    }
}
