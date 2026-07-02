package com.bloquemae.ui.reminders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bloquemae.databinding.ItemReminderTimeBinding

class RemindersAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<String, RemindersAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemReminderTimeBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(time: String) {
            b.timeText.text = time
            b.btnDelete.setOnClickListener { onDelete(time) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemReminderTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(a: String, b: String) = a == b
            override fun areContentsTheSame(a: String, b: String) = a == b
        }
    }
}
