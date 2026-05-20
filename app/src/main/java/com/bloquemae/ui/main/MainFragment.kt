package com.bloquemae.ui.main

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bloquemae.R
import com.bloquemae.data.Block
import com.bloquemae.databinding.FragmentMainBinding
import com.bloquemae.util.WeekUtils

class MainFragment : Fragment() {
    private var _b: FragmentMainBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentMainBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TaskAdapter(
            onToggle = { vm.toggleTask(it); vm.updateCompletionPct() },
            onDelete = { vm.deleteTask(it) }
        )
        b.recycler.adapter = adapter

        vm.activeBlock.observe(viewLifecycleOwner) { updateHeader(it) }
        vm.tasks.observe(viewLifecycleOwner) { adapter.submitList(it) }

        b.fabAdd.setOnClickListener { showAddTaskDialog() }
        b.btnCloseBlock.setOnClickListener { confirmCloseBlock() }
    }

    private fun updateHeader(block: Block?) {
        if (block == null) return
        b.textBlockNumber.text = getString(R.string.block_number, block.number)
        b.textWeekRange.text = WeekUtils.formatWeekRange(block.weekStart, block.weekEnd)
        b.progressBar.progress = (block.completionPct * 100).toInt()
        b.textProgress.text = getString(R.string.progress_pct, (block.completionPct * 100).toInt())
    }

    private fun showAddTaskDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.task_hint)
            setSingleLine()
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_task)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) vm.addTask(text)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmCloseBlock() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.close_block_title)
            .setMessage(R.string.close_block_message)
            .setPositiveButton(R.string.close) { _, _ -> vm.closeBlockAndCreateNew() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
