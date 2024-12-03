package com.capstone.catascan.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.capstone.catascan.data.history.History
import com.capstone.catascan.data.history.HistoryDao
import com.capstone.catascan.data.history.HistoryRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val mHistoryDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryRoomDatabase.getDatabase(application)
        mHistoryDao = db.noteDao()
    }

    fun getAllHistory(): LiveData<List<History>> = mHistoryDao.getAllHistory()
    fun insert(note: History) = executorService.execute { mHistoryDao.insert(note) }
    fun deleteAll() = executorService.execute { mHistoryDao.deleteAll() }
}