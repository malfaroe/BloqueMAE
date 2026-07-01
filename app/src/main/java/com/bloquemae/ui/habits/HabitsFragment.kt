package com.bloquemae.ui.habits

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        adapter = HabitCheckInAdapter(onToggle = { state, checked -> vm.toggle(state.habit, checked) })
        b.recycler.adapter = adapter

        vm.todayCheckins.observe(viewLifecycleOwner) { states ->
            b.emptyText.visibility = if (states.isEmpty()) View.VISIBLE else View.GONE
            b.recycler.visibility = if (states.isEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(states)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
