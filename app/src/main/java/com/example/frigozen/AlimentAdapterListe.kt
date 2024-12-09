package com.example.frigozen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlimentAdapterListe(
    private val context: Context,
    private var items: MutableList<ListAliment>,
    private val onAddToListClick: (ListAliment) -> Unit
) : RecyclerView.Adapter<AlimentAdapterListe.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_aliment_liste, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onAddToListClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ListAliment>) {
        items.clear()
        items.addAll(newItems)
        notifyItemRangeChanged(0, newItems.size)  // Utiliser notifyItemRangeChanged() au lieu de notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ListAliment) {
            val nameTextView = itemView.findViewById<TextView>(R.id.alimentName)
            val quantityTextView = itemView.findViewById<TextView>(R.id.alimentQuantity)
            val caloriesTextView = itemView.findViewById<TextView>(R.id.alimentCalories)

            nameTextView.text = item.name
            quantityTextView.text = "Quantité : ${item.quantity}"
            caloriesTextView.text = "Calories : ${item.calories}"
        }

    }
}
