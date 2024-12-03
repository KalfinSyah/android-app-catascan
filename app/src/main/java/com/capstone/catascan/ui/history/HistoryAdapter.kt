package com.capstone.catascan.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.Utils
import com.capstone.catascan.data.history.History
import com.capstone.catascan.databinding.ItemHistoryBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val listHistory = ArrayList<History>()

    fun setListHistory(listHistory: List<History>) {
        val diffCallback = HistoryDiffCallback(this.listHistory, listHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listHistory.clear()
        this.listHistory.addAll(listHistory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate (
            R.layout.item_history,
            parent,
            false
        )

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemHistoryBinding = ItemHistoryBinding.bind(itemView)

        fun bind(item: History) {
            with(binding) {
                // for the time
                tvHistoryTime.text = Utils.formatDateTime(item.timeStamp)

                // for the img
                Glide.with(itemView.context)
                    .load(item.image)
                    .into(ivHistoryImage)

                // for the result
                val result = item.result.split(", ")
                val predictedClassName = result[0]
                val confidence = result[1]
                val resultBuff= when (predictedClassName) {
                    "immature cataract" -> getString(itemView.context, R.string.info_immature)
                    "mature cataract" -> getString(itemView.context, R.string.info_mature)
                    else -> getString(itemView.context, R.string.infor_normal)
                }
                tvHistoryResult.text = Utils.resultCustomStyling(
                    itemView.context,
                    resultBuff,
                    confidence
                )
            }
        }
    }
}