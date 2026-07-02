package com.bloquemae.ui.habits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bloquemae.data.AppDatabase
import com.bloquemae.data.Habit
import com.bloquemae.data.HabitCheckin
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HabitCheckinState(val habit: Habit, val done: Boolean)

class HabitsViewModel(app: Application) : AndroidViewModel(app) {
    private val habitDao = AppDatabase.get(app).habitDao()
    private val checkinDao = AppDatabase.get(app).habitCheckinDao()

    private val today = WeekUtils.startOfDay()

    val todayCheckins: LiveData<List<HabitCheckinState>> =
        habitDao.activeHabits()
            .combine(checkinDao.checkinsForDate(today)) { habits, checkins ->
                val doneIds = checkins.filter { it.done }.map { it.habitId }.toSet()
                habits.map { HabitCheckinState(it, it.id in doneIds) }
            }
            .asLiveData()

    init {
        viewModelScope.launch { seedDefaultHabitsIfEmpty() }
    }

    private suspend fun seedDefaultHabitsIfEmpty() {
        if (habitDao.count() == 0) {
            habitDao.insertAll(
                listOf(
                    Habit(name = "Colágeno"),
                    Habit(name = "Creatina/Probióticos"),
                    Habit(name = "Daily Review")
                )
            )
        }
    }

    fun toggle(habit: Habit, done: Boolean) {
        viewModelScope.launch {
            checkinDao.upsert(HabitCheckin(habitId = habit.id, date = today, done = done))
        }
    }

    fun addHabit(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { habitDao.insert(Habit(name = trimmed)) }
    }

    fun renameHabit(habit: Habit, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            habitDao.update(habit.copy(name = trimmed, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch { habitDao.delete(habit) }
    }
}
