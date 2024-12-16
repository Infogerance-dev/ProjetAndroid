package com.example.frigozen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoppingListAdapter(
    private val shoppingLists: List<ShoppingList>,
    private val onItemClick: (ShoppingList) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    inner class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listNameTextView: TextView = itemView.findViewById(R.id.listNameTextView)

        fun bind(shoppingList: ShoppingList) {
            listNameTextView.text = shoppingList.name
            itemView.setOnClickListener {
                onItemClick(shoppingList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(shoppingLists[position])
    }

    override fun getItemCount(): Int = shoppingLists.size
}

