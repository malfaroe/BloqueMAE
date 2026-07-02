package com.bloquemae.ui.reminders

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bloquemae.databinding.FragmentRemindersBinding
import java.util.Calendar

class RemindersFragment : Fragment() {
    private var _b: FragmentRemindersBinding? = null
    private val b get() = _b!!
    private val vm: RemindersViewModel by viewModels()
    private lateinit var adapter: RemindersAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRemindersBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RemindersAdapter(onDelete = { vm.removeTime(it) })
        b.recycler.adapter = adapter

        vm.times.observe(viewLifecycleOwner) { times ->
            b.emptyText.visibility = if (times.isEmpty()) View.VISIBLE else View.GONE
            b.recycler.visibility = if (times.isEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(times)
        }

        b.fabAddReminder.setOnClickListener { showTimePicker() }
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> vm.addTime(hour, minute) },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
