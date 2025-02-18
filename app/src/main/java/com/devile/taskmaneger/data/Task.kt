package com.devile.taskmaneger.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    var completionStatus: Boolean = false
) : Parcelable {

    fun daysLeft(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val dueDateObj = sdf.parse(dueDate)
            val today = Calendar.getInstance().time
            val diff = dueDateObj?.time?.minus(today.time) ?: 0
            val days = (diff / (1000 * 60 * 60 * 24)).toInt()

            when {
                days > 0 -> "$days days left"
                days == 0 -> "Due Today"
                else -> "Overdue"
            }
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
