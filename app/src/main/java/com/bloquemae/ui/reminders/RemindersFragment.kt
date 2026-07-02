package com.bloquemae.ui.reminders

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bloquemae.R
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

        b.btnFixNotifPerm.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            )
        }

        b.btnFixExactAlarm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        .setData(Uri.parse("package:${requireContext().packageName}"))
                )
            }
        }

        b.btnTestNotification.setOnClickListener {
            val sent = vm.fireTestNotification()
            val msgRes = if (sent) R.string.test_notification_sent else R.string.test_notification_failed
            Toast.makeText(requireContext(), msgRes, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        b.notifPermCard.visibility = if (vm.hasNotificationPermission()) View.GONE else View.VISIBLE
        b.exactAlarmCard.visibility = if (vm.canScheduleExactAlarms()) View.GONE else View.VISIBLE
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
