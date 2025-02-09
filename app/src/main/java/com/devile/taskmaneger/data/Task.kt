package com.devile.taskmaneger.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val completionStatus: Boolean = false
): Parcelable
