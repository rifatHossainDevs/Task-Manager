package com.devile.taskmaneger.roomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devile.taskmaneger.data.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}