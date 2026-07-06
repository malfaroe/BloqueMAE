package com.bloquemae.ui.dailyreview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bloquemae.data.AppDatabase
import com.bloquemae.data.DailyReview
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.launch

class DailyReviewViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dailyReviewDao()

    val today = WeekUtils.startOfDay()

    val pastReviews: LiveData<List<DailyReview>> =
        dao.allReviews().asLiveData()

    suspend fun getByDate(date: Long): DailyReview? = dao.getByDate(date)

    fun save(date: Long, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            dao.upsert(DailyReview(date = date, text = trimmed, updatedAt = System.currentTimeMillis()))
        }
    }

    fun delete(review: DailyReview) {
        viewModelScope.launch { dao.delete(review) }
    }
}
