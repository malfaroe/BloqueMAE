package com.bloquemae.ui.habits

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bloquemae.R
import com.bloquemae.data.Habit
import com.bloquemae.databinding.FragmentHabitsBinding

class HabitsFragment : Fragment() {
    private var _b: FragmentHabitsBinding? = null
    private val b get() = _b!!
    private val vm: HabitsViewModel by viewModels()
    private lateinit var adapter: HabitCheckInAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHabitsBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HabitCheckInAdapter(
            onToggle = { state, checked -> vm.toggle(state.habit, checked) },
            onEdit = { showEditHabitDialog(it) },
            onDelete = { confirmDeleteHabit(it) }
        )
        b.recycler.adapter = adapter

        vm.todayCheckins.observe(viewLifecycleOwner) { states ->
            b.emptyText.visibility = if (states.isEmpty()) View.VISIBLE else View.GONE
            b.recycler.visibility = if (states.isEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(states)
        }

        b.fabAddHabit.setOnClickListener { showAddHabitDialog() }
        b.btnReminders.setOnClickListener { findNavController().navigate(R.id.remindersFragment) }
    }

    private fun showAddHabitDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.habit_hint)
            setSingleLine()
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_habit)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ -> vm.addHabit(input.text.toString()) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val input = EditText(requireContext()).apply {
            setText(habit.name)
            setSingleLine()
            setSelection(text.length)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_habit)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ -> vm.renameHabit(habit, input.text.toString()) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDeleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_habit_title)
            .setMessage(R.string.delete_habit_message)
            .setPositiveButton(R.string.delete) { _, _ -> vm.deleteHabit(habit) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
