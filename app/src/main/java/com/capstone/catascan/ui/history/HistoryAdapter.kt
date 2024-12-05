package com.capstone.catascan.ui.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailHistoryActivity::class.java)
                    intent.putExtra("extra_history_time", item.timeStamp)
                    intent.putExtra("extra_history_image", item.image)
                    intent.putExtra("extra_history_predicted_class_name", predictedClassName)
                    intent.putExtra("extra_history_confidence", confidence)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}