package com.bloquemae.ui.dailyreview

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bloquemae.R
import com.bloquemae.data.DailyReview
import com.bloquemae.databinding.FragmentDailyReviewBinding
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.launch

class DailyReviewFragment : Fragment() {
    private var _b: FragmentDailyReviewBinding? = null
    private val b get() = _b!!
    private val vm: DailyReviewViewModel by viewModels()
    private lateinit var adapter: DailyReviewAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDailyReviewBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DailyReviewAdapter(onClick = { showEditDialog(it) })
        b.recycler.adapter = adapter

        b.textToday.text = WeekUtils.formatDay(vm.today)
        b.editToday.movementMethod = ScrollingMovementMethod.getInstance()
        viewLifecycleOwner.lifecycleScope.launch {
            vm.getByDate(vm.today)?.let { b.editToday.setText(it.text) }
        }

        b.btnSaveToday.setOnClickListener {
            vm.save(vm.today, b.editToday.text.toString())
            hideKeyboard(b.editToday)
            Toast.makeText(requireContext(), R.string.daily_review_saved, Toast.LENGTH_SHORT).show()
        }

        vm.pastReviews.observe(viewLifecycleOwner) { reviews ->
            val past = reviews.filter { it.date != vm.today }
            b.emptyText.visibility = if (past.isEmpty()) View.VISIBLE else View.GONE
            b.recycler.visibility = if (past.isEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(past)
        }
    }

    private fun showEditDialog(review: DailyReview) {
        val input = EditText(requireContext()).apply {
            setText(review.text)
            minLines = 3
            maxLines = 10
            movementMethod = ScrollingMovementMethod.getInstance()
            gravity = Gravity.TOP
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setSelection(text.length)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(WeekUtils.formatDay(review.date))
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                vm.save(review.date, input.text.toString())
                Toast.makeText(requireContext(), R.string.daily_review_saved, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.delete) { _, _ -> confirmDelete(review) }
            .show()
    }

    private fun confirmDelete(review: DailyReview) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_review_title)
            .setMessage(R.string.delete_review_message)
            .setPositiveButton(R.string.delete) { _, _ -> vm.delete(review) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
