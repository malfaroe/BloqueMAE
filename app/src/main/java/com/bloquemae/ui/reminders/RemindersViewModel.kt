package com.bloquemae.ui.reminders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bloquemae.util.ReminderPrefs
import com.bloquemae.worker.HabitReminderScheduler
import com.bloquemae.worker.HabitReminderWorker

class RemindersViewModel(app: Application) : AndroidViewModel(app) {
    private val _times = MutableLiveData(ReminderPrefs.getTimes(app))
    val times: LiveData<List<String>> = _times

    fun hasNotificationPermission(): Boolean =
        HabitReminderWorker.hasNotificationPermission(getApplication())

    fun canScheduleExactAlarms(): Boolean =
        HabitReminderScheduler.canScheduleExactAlarms(getApplication())

    fun fireTestNotification(): Boolean =
        HabitReminderWorker.showNotification(getApplication(), listOf("Prueba"))

    fun addTime(hour: Int, minute: Int) {
        val time = "%02d:%02d".format(hour, minute)
        ReminderPrefs.addTime(getApplication(), time)
        HabitReminderScheduler.rescheduleFor(getApplication(), time)
        refresh()
    }

    fun removeTime(time: String) {
        ReminderPrefs.removeTime(getApplication(), time)
        HabitReminderScheduler.cancel(getApplication(), time)
        refresh()
    }

    private fun refresh() {
        _times.value = ReminderPrefs.getTimes(getApplication())
    }
}
