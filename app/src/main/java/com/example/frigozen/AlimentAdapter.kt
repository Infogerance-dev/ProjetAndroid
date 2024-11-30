package com.example.frigozen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlimentsAdapter(
    private var items: List<Any>, // Peut être une liste de String ou d'Aliment
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == VIEW_TYPE_CATEGORY) R.layout.item_category else R.layout.item_aliment,
            parent,
            false
        )
        return if (viewType == VIEW_TYPE_CATEGORY) {
            CategoryViewHolder(view)
        } else {
            AlimentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is CategoryViewHolder && item is String) {
            holder.bind(item)
        } else if (holder is AlimentViewHolder && item is Aliment) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) VIEW_TYPE_CATEGORY else VIEW_TYPE_ALIMENT
    }

    fun updateItems(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)

        fun bind(category: String) {
            tvCategoryName.text = category
            itemView.setOnClickListener { onItemClick(category) }
        }
    }

    inner class AlimentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAlimentName: TextView = view.findViewById(R.id.tvAlimentName)

        fun bind(aliment: Aliment) {
            tvAlimentName.text = aliment.name
            itemView.setOnClickListener { onItemClick(aliment) }
        }
    }

    companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_ALIMENT = 1
    }
}

