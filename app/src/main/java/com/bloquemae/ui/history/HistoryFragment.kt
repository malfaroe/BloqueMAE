package com.bloquemae.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bloquemae.databinding.FragmentHistoryBinding

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
        adapter = HistoryAdapter()
        b.recycler.adapter = adapter

        vm.closedBlocks.observe(viewLifecycleOwner) { blocks ->
            b.emptyText.visibility = if (blocks.isEmpty()) View.VISIBLE else View.GONE
            b.recycler.visibility = if (blocks.isEmpty()) View.GONE else View.VISIBLE
            b.barChart.visibility = if (blocks.isEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(blocks)
            b.barChart.setData(blocks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
