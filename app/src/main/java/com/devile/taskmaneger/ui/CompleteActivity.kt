package com.devile.taskmaneger.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.ActivityCompleteBinding
import com.devile.taskmaneger.recyclerView.Adapter
import com.devile.taskmaneger.roomDatabase.TaskDao
import com.devile.taskmaneger.roomDatabase.TaskDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class CompleteActivity : AppCompatActivity(), Adapter.HandleClickListener {
    private lateinit var binding: ActivityCompleteBinding
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: Adapter
    private lateinit var taskList: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteBinding.inflate(layoutInflater)
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
    }

    private fun fetchTasks() {
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Ensure correct format

        taskList = taskDao.getCompleteTask().toMutableList()


        taskList.sortWith(compareBy { task ->
            try {
                dateFormat.parse(task.dueDate)
            } catch (e: ParseException) {
                null
            }
        })

        adapter.updateData(taskList)
    }

    override fun editClickListener(task: Task) {

    }

    override fun deleteClickListener(task: Task) {
        taskDao.deleteTask(task)
        Toast.makeText(this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
        fetchTasks()
        setHomePage()
    }

    private fun setHomePage() {
        adapter = Adapter(taskList, this)
        binding.rvLayout.layoutManager = LinearLayoutManager(this)
        binding.rvLayout.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun completeClickListener(task: Task, isComplete: Boolean) {

        taskDao.updateCompleteStatus(task.taskId, isComplete)

        fetchTasks()

        adapter.notifyDataSetChanged()

        Toast.makeText(
            this,
            if (!isComplete) "Task Moved Back to Incomplete" else "Task Marked as Completed",
            Toast.LENGTH_SHORT
        ).show()
    }


}