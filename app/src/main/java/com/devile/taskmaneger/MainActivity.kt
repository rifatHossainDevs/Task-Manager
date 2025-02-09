package com.devile.taskmaneger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.ActivityMainBinding
import com.devile.taskmaneger.recyclerView.Adapter
import com.devile.taskmaneger.roomDatabase.TaskDao
import com.devile.taskmaneger.roomDatabase.TaskDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Adapter.HandleClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: Adapter
    private lateinit var completeTaskList: MutableList<Task>
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

        taskList = mutableListOf()
        adapter = Adapter(taskList, this)
        binding.rvLayout.adapter = adapter
        fetchTasks()

        binding.btnAddUser.setOnClickListener {
            val addActivityIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(addActivityIntent)
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.completeTask -> {
                val completeIntent = Intent(this@MainActivity, CompleteActivity::class.java)
                completeIntent.putParcelableArrayListExtra(
                    "completeTask",
                    ArrayList(completeTaskList)
                )
                startActivity(completeIntent)
                true
            }
            R.id.sort_by_due_date -> {
                sortTasksByDueDate()  // Sort by Due Date when selected
                true
            }
            R.id.sort_by_title -> {
                sortTasksByTitle()  // Sort by Title when selected
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchTasks() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Ensure correct format

        taskList = taskDao.getAllTask().toMutableList()
        completeTaskList = taskList.filter { it.completionStatus }.toMutableList()

        // Sort tasks by due date
        taskList.sortWith(compareBy { task ->
            try {
                dateFormat.parse(task.dueDate) // Parse the stored format
            } catch (e: ParseException) {
                null // Ignore invalid dates
            }
        })

        adapter.updateData(taskList)
    }



    private fun sortTasksByDueDate() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Use this format to parse date

        // Sort the task list by due date, handle possible parsing errors
        try {
            taskList.sortBy { task ->
                try {
                    format.parse(task.dueDate)  // Parse the due date
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error parsing date: ${task.dueDate}", e)
                    null  // Return null if parsing fails
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error sorting tasks by due date", e)
        }

        adapter.updateData(taskList)
    }

    private fun sortTasksByTitle() {
        taskList.sortBy { it.title }  // Sort tasks by title
        adapter.updateData(taskList)
    }

    private fun setHomePage() {
        adapter = Adapter(taskList, this)
        binding.rvLayout.layoutManager = LinearLayoutManager(this)
        binding.rvLayout.adapter = adapter
    }

    override fun deleteClickListener(task: Task) {
        taskDao.deleteTask(task)
        Toast.makeText(this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
        fetchTasks()
        setHomePage()
    }

    override fun editClickListener(task: Task) {
        val editIntent = Intent(this@MainActivity, AddActivity::class.java)
        editIntent.putExtra(AddActivity.editKey, task)
        startActivity(editIntent)
    }

    override fun completeClickListener(task: Task, isComplete: Boolean) {
        taskDao.updateCompleteStatus(task.taskId, isComplete)
        fetchTasks()
        setHomePage()
        Toast.makeText(this, "Task Completed", Toast.LENGTH_SHORT).show()
    }
}
