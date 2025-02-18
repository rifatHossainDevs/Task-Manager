package com.devile.taskmaneger.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.TaskLayoutBinding

class Adapter(private var taskList: MutableList<Task>, val listener: HandleClickListener) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(val binding: TaskLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(tasks: List<Task>) {
        this.taskList = tasks.toMutableList()
        notifyDataSetChanged()
    }

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

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding.apply {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvDueDate.text = task.dueDate

            tvLeftDate.text = task.daysLeft()

            deleteBtn.setOnClickListener {
                listener.deleteClickListener(task)
            }

            root.setOnClickListener {
                listener.editClickListener(task)
            }

            completionCB.setOnCheckedChangeListener(null)
            completionCB.isChecked = task.completionStatus

            completionCB.setOnCheckedChangeListener { _, isChecked ->
                listener.completeClickListener(task, isChecked)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Task>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }
}
