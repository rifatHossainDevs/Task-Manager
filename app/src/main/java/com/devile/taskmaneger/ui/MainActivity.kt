package com.devile.taskmaneger.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.devile.taskmaneger.R
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
        binding.rvLayout.layoutManager = LinearLayoutManager(this)  // Set layout manager
        binding.rvLayout.adapter = adapter

        fetchTasks()

        binding.btnAddUser.setOnClickListener {
            val addActivityIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(addActivityIntent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        setSupportActionBar(binding.toolbar)
    }

    fun filterList(query: String?) {
        if (query != null) {
            val filteredList = taskList.filter {
                it.title.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }
            adapter.setFilteredList(filteredList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.completeTask -> {
                val completeIntent = Intent(this@MainActivity, CompleteActivity::class.java)
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        taskList = taskDao.getUnCompleteTask().toMutableList()

        // Sort tasks by due date
        taskList.sortWith(compareBy { task ->
            try {
                dateFormat.parse(task.dueDate)
            } catch (e: ParseException) {
                null
            }
        })

        adapter.updateData(taskList)
    }

    private fun sortTasksByDueDate() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            taskList.sortBy { task ->
                try {
                    format.parse(task.dueDate)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error parsing date: ${task.dueDate}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error sorting tasks by due date", e)
        }

        adapter.updateData(taskList)
    }

    private fun sortTasksByTitle() {
        taskList.sortBy { it.title }
        adapter.updateData(taskList)
    }

    override fun deleteClickListener(task: Task) {
        taskDao.deleteTask(task)
        Toast.makeText(this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
        fetchTasks()
    }

    override fun editClickListener(task: Task) {
        val editIntent = Intent(this@MainActivity, AddActivity::class.java)
        editIntent.putExtra(AddActivity.editKey, task)
        startActivity(editIntent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun completeClickListener(task: Task, isComplete: Boolean) {
        taskDao.updateCompleteStatus(task.taskId, isComplete)
        fetchTasks()
        adapter.notifyDataSetChanged()

        Toast.makeText(
            this,
            if (isComplete) "Task Completed" else "Task Marked as Incomplete",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ✅ Refresh task list when returning from another screen
    override fun onResume() {
        super.onResume()
        fetchTasks()  // Reload tasks on returning to MainActivity
    }
}
