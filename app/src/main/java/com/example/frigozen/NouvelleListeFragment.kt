package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NouvelleListeFragment : Fragment(R.layout.fragment_nouvelle_liste) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlimentsAdapter
    private var isShowingCategories = true
    private var currentCategory: String? = null

    private val alimentsList = listOf(
        Aliment("Pomme", R.drawable.bilan_nutritif_icon, "Fruits"),
        Aliment("Banane", R.drawable.bilan_nutritif_icon, "Fruits"),
        Aliment("Carotte", R.drawable.bilan_nutritif_icon, "Légumes"),
        Aliment("Tomate", R.drawable.bilan_nutritif_icon, "Légumes"),
        Aliment("Poulet", R.drawable.bilan_nutritif_icon, "Viandes")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Grouper les aliments par catégorie
        val categories = alimentsList.map { it.category }.distinct()

        // Configuration RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Adapter initial
        adapter = AlimentsAdapter(emptyList()) { item ->
            if (isShowingCategories) {
                // Passer à l'affichage des aliments
                currentCategory = item as String
                updateRecyclerViewForCategory(item)
            } else {
                // Action sur un aliment sélectionné
                val aliment = item as Aliment
                Toast.makeText(requireContext(), "${aliment.name} sélectionné.", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        // Afficher uniquement les catégories au départ
        updateRecyclerViewForCategories(categories)

        // Bouton de retour
        view.findViewById<Button>(R.id.btnOkList).apply {
            text = "Retour"
            visibility = View.GONE
            setOnClickListener {
                updateRecyclerViewForCategories(categories)
                isShowingCategories = true
                visibility = View.GONE
            }
        }
    }

    private fun updateRecyclerViewForCategories(categories: List<String>) {
        adapter.updateItems(categories)
        isShowingCategories = true
        recyclerView.adapter = adapter
    }

    private fun updateRecyclerViewForCategory(category: String) {
        val alimentsInCategory = alimentsList.filter { it.category == category }
        adapter.updateItems(alimentsInCategory)
        isShowingCategories = false
        view?.findViewById<Button>(R.id.btnOkList)?.visibility = View.VISIBLE
    }
}

