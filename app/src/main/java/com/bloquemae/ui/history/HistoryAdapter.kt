package com.bloquemae.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bloquemae.R
import com.bloquemae.data.BlockWithStats
import com.bloquemae.databinding.ItemHistoryBlockBinding
import com.bloquemae.util.WeekUtils

class HistoryAdapter : ListAdapter<BlockWithStats, HistoryAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemHistoryBlockBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: BlockWithStats) {
            b.textBlockNumber.text = b.root.context.getString(R.string.block_number, item.number)
            b.textWeekRange.text = WeekUtils.formatWeekRange(item.weekStart, item.weekEnd)
            b.textTaskCount.text = b.root.context.getString(
                R.string.task_count, item.doneTasks, item.totalTasks
            )
            val pct = (item.completionPct * 100).toInt()
            b.textPct.text = b.root.context.getString(R.string.progress_pct, pct)
            b.progressBar.progress = pct
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemHistoryBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<BlockWithStats>() {
            override fun areItemsTheSame(a: BlockWithStats, b: BlockWithStats) = a.id == b.id
            override fun areContentsTheSame(a: BlockWithStats, b: BlockWithStats) = a == b
        }
    }
}
