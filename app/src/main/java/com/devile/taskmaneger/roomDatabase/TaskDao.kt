package com.devile.taskmaneger.roomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.devile.taskmaneger.data.Task

@Dao
interface TaskDao {
    @Insert
    fun addTask(task: Task)

    @Query("SELECT * FROM task")
    fun getAllTask(): List<Task>

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)


    @Query("SELECT * FROM Task WHERE completionStatus = 1")
    fun getCompleteTask(): MutableList<Task>

    @Query("UPDATE Task SET completionStatus = :isComplete WHERE taskId = :taskId")
    fun updateCompleteStatus(taskId: Int, isComplete: Boolean)
}