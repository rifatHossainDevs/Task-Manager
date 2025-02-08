package com.devile.taskmaneger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val completionStatus: Boolean
)
