package com.devile.taskmaneger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.ActivityMainBinding
import com.devile.taskmaneger.roomDatabase.TaskDao
import com.devile.taskmaneger.roomDatabase.TaskDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var taskDao: TaskDao
    private lateinit var taskList: MutableList<Task>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "taskDb"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        taskDao = db.taskDao()
        val task = Task(0, "title", "description", "12.12.12", true)

        taskDao.addTask(task)

        setHomePage()
        fetchTaskList()
    }

    private fun fetchTaskList() {

    }

    private fun setHomePage() {

    }
}