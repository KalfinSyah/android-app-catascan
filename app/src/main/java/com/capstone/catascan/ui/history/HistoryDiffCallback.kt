package com.capstone.catascan.ui.history

import androidx.recyclerview.widget.DiffUtil
import com.capstone.catascan.data.history.History

class HistoryDiffCallback(private val oldHistoryList: List<History>, private val newHistoryList: List<History>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldHistoryList.size
    override fun getNewListSize(): Int = newHistoryList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldHistoryList[oldItemPosition].id == newHistoryList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldHistoryList[oldItemPosition]
        val newHistory = newHistoryList[newItemPosition]
        val oldResult = oldHistory.result.split("|")
        val newResult = newHistory.result.split("|")
        return oldResult[0] == newResult[0] && oldHistory.image == newHistory.image
    }
}