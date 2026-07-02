package com.bloquemae.ui.history

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bloquemae.R
import com.bloquemae.data.BlockWithStats
import com.bloquemae.data.Task
import com.bloquemae.databinding.DialogBlockTasksBinding
import com.bloquemae.databinding.FragmentHistoryBinding
import com.bloquemae.databinding.ItemTaskReadonlyBinding
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private val vm: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHistoryBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HistoryAdapter(onClick = { showBlockTasks(it) })
        b.recycler.adapter = adapter

        vm.closedBlocks.observe(viewLifecycleOwner) { blocks ->
            val hasBlocks = blocks.isNotEmpty()
            b.emptyText.visibility = if (hasBlocks) View.GONE else View.VISIBLE
            b.recycler.visibility = if (hasBlocks) View.VISIBLE else View.GONE
            b.listLabel.visibility = if (hasBlocks) View.VISIBLE else View.GONE
            b.barChart.visibility = if (hasBlocks) View.VISIBLE else View.GONE
            b.chartLabel.visibility = if (hasBlocks) View.VISIBLE else View.GONE
            adapter.submitList(blocks)
            b.barChart.setData(blocks)
        }

        vm.habitStats.observe(viewLifecycleOwner) { stats ->
            b.habitReportCard.visibility = if (stats.isEmpty()) View.GONE else View.VISIBLE
            b.habitReportBody.text = stats.joinToString("\n") { stat ->
                val row = getString(R.string.habit_report_row, stat.habitName, stat.doneThisWeek, stat.totalThisWeek)
                val vsLast = getString(R.string.habit_report_vs_last, stat.doneLastWeek, stat.totalLastWeek)
                "$row ($vsLast)"
            }
        }
    }

    private fun showBlockTasks(block: BlockWithStats) {
        viewLifecycleOwner.lifecycleScope.launch {
            val tasks = vm.tasksForBlock(block.id)
            val dialogBinding = DialogBlockTasksBinding.inflate(layoutInflater)

            if (tasks.isEmpty()) {
                dialogBinding.taskContainer.addView(emptyRow())
            } else {
                tasks.forEach { dialogBinding.taskContainer.addView(taskRow(it)) }
            }

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.block_number, block.number))
                .setView(dialogBinding.root)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun taskRow(task: Task): View {
        val row = ItemTaskReadonlyBinding.inflate(layoutInflater)
        row.statusMark.text = if (task.isDone) "✓" else "✗"
        row.statusMark.setTextColor(
            resources.getColor(if (task.isDone) R.color.colorAccent else R.color.colorMuted, null)
        )
        row.taskText.text = task.text
        row.taskText.alpha = if (task.isDone) 0.6f else 1f
        return row.root
    }

    private fun emptyRow(): View {
        val text = TextView(requireContext())
        text.text = getString(R.string.block_tasks_empty)
        text.setTextColor(resources.getColor(R.color.colorOnBackground, null))
        return text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
