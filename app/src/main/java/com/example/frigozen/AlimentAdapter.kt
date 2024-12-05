package com.example.frigozen

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView


class AlimentAdapter(
    private val context: Context,
    private val items: List<ListItem>,
    private val onAddToListClick: (Aliment) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Category -> 0
            is ListItem.AlimentItem -> 1
            else -> throw IllegalArgumentException("Type inconnu à la position $position")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) { // Catégorie
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            CategoryViewHolder(view)
        } else { // Aliment
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aliment, parent, false)
            AlimentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Category -> (holder as CategoryViewHolder).bind(item.name)
            is ListItem.AlimentItem -> (holder as AlimentViewHolder).bind(item.aliment)
            else -> throw IllegalArgumentException("Type de vue inconnu à la position $position")
        }
    }


    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)

        fun bind(category: String) {
            tvCategoryName.text = category
        }
    }

    inner class AlimentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivAliment: ImageView = view.findViewById(R.id.ivAliment)
        private val tvAlimentName: TextView = view.findViewById(R.id.tvAlimentName)
        private val btnAddToList: Button = view.findViewById(R.id.btnAddToList)




        fun bind(aliment: Aliment) {
            ivAliment.setImageResource(aliment.imageResId)
            tvAlimentName.text = aliment.name
            btnAddToList.setOnClickListener {
                // Utilise itemView.context pour obtenir un contexte valide
                qshowAddToListDialog(itemView.context, aliment)
            }
        }


    }
}
