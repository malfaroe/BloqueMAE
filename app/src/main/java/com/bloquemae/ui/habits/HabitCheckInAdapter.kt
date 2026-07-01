package com.bloquemae.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bloquemae.databinding.ItemHabitCheckinBinding

class HabitCheckInAdapter(
    private val onToggle: (HabitCheckinState, Boolean) -> Unit
) : ListAdapter<HabitCheckinState, HabitCheckInAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemHabitCheckinBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(state: HabitCheckinState) {
            b.habitName.text = state.habit.name
            b.checkBox.setOnCheckedChangeListener(null)
            b.checkBox.isChecked = state.done
            b.checkBox.setOnCheckedChangeListener { _, checked -> onToggle(state, checked) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemHabitCheckinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<HabitCheckinState>() {
            override fun areItemsTheSame(a: HabitCheckinState, b: HabitCheckinState) = a.habit.id == b.habit.id
            override fun areContentsTheSame(a: HabitCheckinState, b: HabitCheckinState) = a == b
        }
    }
}
