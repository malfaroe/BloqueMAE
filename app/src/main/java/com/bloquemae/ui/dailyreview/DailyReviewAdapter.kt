package com.bloquemae.ui.dailyreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bloquemae.data.DailyReview
import com.bloquemae.databinding.ItemDailyReviewBinding
import com.bloquemae.util.WeekUtils

class DailyReviewAdapter(
    private val onClick: (DailyReview) -> Unit
) : ListAdapter<DailyReview, DailyReviewAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemDailyReviewBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: DailyReview) {
            b.textDate.text = WeekUtils.formatDay(item.date)
            b.textBody.text = item.text
            b.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemDailyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DailyReview>() {
            override fun areItemsTheSame(a: DailyReview, b: DailyReview) = a.date == b.date
            override fun areContentsTheSame(a: DailyReview, b: DailyReview) = a == b
        }
    }
}
