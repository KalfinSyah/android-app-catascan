package com.capstone.catascan.ui.home.cataracttypes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.data.CataractType

class CataractAdapter(private val listTypes: List<CataractType>) : RecyclerView.Adapter<CataractAdapter.CataractViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CataractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cataract, parent, false)
        return CataractViewHolder(view)
    }

    override fun onBindViewHolder(holder: CataractViewHolder, position: Int) {
        val item = listTypes[position]
        holder.name.text = item.name
        holder.description.text = item.description
        Glide.with(holder.itemView.context)
            .load(item.photo)
            .into(holder.photo)

        // Set listener untuk item klik
        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, CataractDetailActivity::class.java)
            intent.putExtra("cataract_type", item) // Kirim data ke aktivitas detail
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listTypes.size
    }

    class CataractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_item_name)
        val description: TextView = itemView.findViewById(R.id.tv_item_description)
        val photo: ImageView = itemView.findViewById(R.id.img_item_photo)
    }
}