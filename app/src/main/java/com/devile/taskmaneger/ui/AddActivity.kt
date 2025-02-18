package com.devile.taskmaneger.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.ActivityAddBinding
import com.devile.taskmaneger.roomDatabase.TaskDao
import com.devile.taskmaneger.roomDatabase.TaskDatabase
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class AddActivity : AppCompatActivity() {
    companion object {
        const val editKey = "edit"
        const val newTask = "New Task"
        const val updateTask = "Update Task"
    }

    private var taskId = 0

    private lateinit var binding: ActivityAddBinding
    private lateinit var taskDao: TaskDao
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "taskDb"
        ).allowMainThreadQueries().build()

        taskDao = db.taskDao()

        if (intent.hasExtra(editKey)) {
            binding.tvActivityName.text = updateTask
            val task = intent.getParcelableExtra<Task>(editKey)
            binding.apply {
                etTitle.setText(task?.title)
                etDueDate.setText(task?.dueDate)
                etDescription.setText(task?.description)
                taskId = task?.taskId ?: 0
            }

        } else {
            binding.tvActivityName.text = newTask
        }

        binding.btnAddTask.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val dueDate = binding.etDueDate.text.toString()
            val description = binding.etDescription.text.toString()

            if (binding.tvActivityName.text.toString() == newTask) {
                addTask(title, dueDate, description)
            } else {
                updateTask(title, dueDate, description)
            }

            setHomePage()

        }

        binding.etDueDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnBack.setOnClickListener {
            val mainActivityIntent = Intent(this@AddActivity, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

    }

    private fun setHomePage() {
        val homeIntent = Intent(this@AddActivity, MainActivity::class.java)
        startActivity(homeIntent)
    }

    private fun addTask(title: String, dueDate: String, description: String) {
        val task = Task(0, title, description, formatDate(dueDate))
        Log.d("TAG", "addTask: $title")
        taskDao.addTask(task)
    }

    private fun updateTask(title: String, dueDate: String, description: String) {
        val task = Task(taskId, title, description, formatDate(dueDate))
        taskDao.updateTask(task)
    }

    private fun formatDate(date: String): String {
        return try {
            val inputFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            date
        }
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = sdf.format(calendar.time)
            binding.etDueDate.setText(selectedDate)
        }, year, month, day).show()
    }

}