package com.devile.taskmaneger.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.TaskLayoutBinding
import kotlin.math.tan

class Adapter(private var task: MutableList<Task>, val listener: HandleClickListener) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(val binding: TaskLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    interface HandleClickListener {
        fun editClickListener(task: Task)
        fun deleteClickListener(task: Task)
        fun completeClickListener(task: Task, isComplete: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TaskLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = task.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = task[position]
        holder.binding.apply {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvDueDate.text = task.dueDate

            deleteBtn.setOnClickListener {
                listener.deleteClickListener(task)
            }

            root.setOnClickListener {
                listener.editClickListener(task)
            }

            completionCB.setOnClickListener {
                val newCompleteStatus = !task.completionStatus
                listener.completeClickListener(task, newCompleteStatus)
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Task>) {
        task.clear()
        task.addAll(newList)
        notifyDataSetChanged() // Refresh UI
    }
}