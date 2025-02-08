package com.devile.taskmaneger.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devile.taskmaneger.data.Task
import com.devile.taskmaneger.databinding.TaskLayoutBinding

class Adapter(private val task: List<Task>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(val binding: TaskLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = task.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        task[position].let {
            holder.binding.tvTitle.text = it.title
            holder.binding.tvDescription.text = it.description
            holder.binding.tvDueDate.text = it.dueDate
        }
    }
}