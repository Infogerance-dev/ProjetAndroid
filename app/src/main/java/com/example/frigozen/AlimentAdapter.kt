package com.example.frigozen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Aliment(val name: String, val imageResourceId: Int) // Utilisez un ID de ressource drawable

class AlimentsAdapter(
    private val aliments: List<Aliment>,
    private val onAddToListClick: (Aliment) -> Unit
) : RecyclerView.Adapter<AlimentsAdapter.AlimentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aliment, parent, false)
        return AlimentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlimentsViewHolder, position: Int) {
        val aliment = aliments[position]
        holder.bind(aliment)
    }

    override fun getItemCount(): Int = aliments.size

    inner class AlimentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivAliment: ImageView = view.findViewById(R.id.ivAliment)
        private val tvAlimentName: TextView = view.findViewById(R.id.tvAlimentName)
        private val btnAddToList: Button = view.findViewById(R.id.btnAddToList)

        fun bind(aliment: Aliment) {
            // Charger l'image
            ivAliment.setImageResource(aliment.imageResourceId)  // Charge l'image vectorielle

            // Afficher le nom de l'aliment
            tvAlimentName.text = aliment.name

            // Ajouter un aliment à la liste
            btnAddToList.setOnClickListener {
                onAddToListClick(aliment)
            }
        }
    }
}
