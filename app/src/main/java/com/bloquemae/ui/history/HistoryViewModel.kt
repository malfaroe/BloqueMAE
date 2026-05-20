package com.bloquemae.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.bloquemae.data.AppDatabase

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val blockDao = AppDatabase.get(app).blockDao()
    val closedBlocks = blockDao.closedBlocksWithStats().asLiveData()
}
