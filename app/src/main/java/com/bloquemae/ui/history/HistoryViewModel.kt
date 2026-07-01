package com.bloquemae.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.bloquemae.data.AppDatabase
import com.bloquemae.data.Habit
import com.bloquemae.util.WeekUtils

data class HabitWeekStat(
    val habitName: String,
    val doneThisWeek: Int,
    val totalThisWeek: Int,
    val doneLastWeek: Int,
    val totalLastWeek: Int
)

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val blockDao = AppDatabase.get(app).blockDao()
    private val habitDao = AppDatabase.get(app).habitDao()
    private val checkinDao = AppDatabase.get(app).habitCheckinDao()

    val closedBlocks = blockDao.closedBlocksWithStats().asLiveData()

    // HistoryFragment is recreated each time the user navigates back to this tab
    // (Navigation Component default behavior), so this recomputes on every visit.
    val habitStats: LiveData<List<HabitWeekStat>> = liveData {
        emit(computeHabitWeekStats())
    }

    private suspend fun computeHabitWeekStats(): List<HabitWeekStat> {
        val habits = habitDao.allHabitsOnce()
        val thisWeekStart = WeekUtils.currentWeekStart()
        val thisWeekEndDay = WeekUtils.startOfDay(WeekUtils.currentWeekEnd())
        val lastWeekStart = WeekUtils.previousWeekStart()
        val lastWeekEndDay = WeekUtils.startOfDay(WeekUtils.previousWeekEnd())

        return habits.mapNotNull { habit ->
            val thisWeek = weekCount(habit, thisWeekStart, thisWeekEndDay)
            val lastWeek = weekCount(habit, lastWeekStart, lastWeekEndDay)
            if (thisWeek.second == 0 && lastWeek.second == 0) return@mapNotNull null
            HabitWeekStat(
                habitName = habit.name,
                doneThisWeek = thisWeek.first,
                totalThisWeek = thisWeek.second,
                doneLastWeek = lastWeek.first,
                totalLastWeek = lastWeek.second
            )
        }
    }

    // Returns done/total for a habit within [weekStart, weekEndDay], clamped to the
    // days the habit was actually active that week (created mid-week or paused mid-week).
    private suspend fun weekCount(habit: Habit, weekStart: Long, weekEndDay: Long): Pair<Int, Int> {
        val habitStartDay = maxOf(weekStart, WeekUtils.startOfDay(habit.createdAt))
        val habitEndDay = if (!habit.active) {
            minOf(weekEndDay, WeekUtils.startOfDay(habit.updatedAt) - 86_400_000L)
        } else {
            weekEndDay
        }
        val total = WeekUtils.daysBetweenInclusive(habitStartDay, habitEndDay)
        if (total == 0) return 0 to 0
        val done = checkinDao.checkinsForHabitInRange(habit.id, habitStartDay, habitEndDay)
            .count { it.done }
        return done to total
    }
}
