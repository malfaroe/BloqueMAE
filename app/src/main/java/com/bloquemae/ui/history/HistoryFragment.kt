package com.bloquemae.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bloquemae.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHistoryBinding.inflate(i, c, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
