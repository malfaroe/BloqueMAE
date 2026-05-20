package com.bloquemae.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bloquemae.data.Task
import com.bloquemae.databinding.ItemTaskBinding

class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(task: Task) {
            b.checkBox.isChecked = task.isDone
            b.taskText.text = task.text
            b.taskText.alpha = if (task.isDone) 0.4f else 1f
            b.carryOverIcon.visibility = if (task.isCarriedOver) android.view.View.VISIBLE else android.view.View.GONE
            b.checkBox.setOnCheckedChangeListener(null)
            b.checkBox.setOnCheckedChangeListener { _, _ -> onToggle(task) }
            b.btnDelete.setOnClickListener { onDelete(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(a: Task, b: Task) = a.id == b.id
            override fun areContentsTheSame(a: Task, b: Task) = a == b
        }
    }
}
