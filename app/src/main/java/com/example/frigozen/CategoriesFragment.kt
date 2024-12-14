package com.example.frigozen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frigozen.R
import com.example.frigozen.CategoriesAdapter

class CategoriesFragment : Fragment(R.layout.fragment_categories) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val categories = listOf("Fruits", "Légumes", "Viandes", "Poissons") // Exemple de catégories

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation du RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Créer et définir l'adaptateur
        categoriesAdapter = CategoriesAdapter(categories) { category ->
            // Action lorsque l'utilisateur clique sur une catégorie
            navigateToAliments(category)
        }
        recyclerView.adapter = categoriesAdapter
    }

    private fun navigateToAliments(category: String) {
        val bundle = Bundle()
        bundle.putString("selectedCategory", category)

        // Créer un fragment qui affichera les aliments en fonction de la catégorie
        val alimentsFragment = AlimentsFragment()
        alimentsFragment.arguments = bundle

        // Navigation vers le fragment des aliments
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, alimentsFragment) // Remplace le conteneur de fragment
            .addToBackStack(null)
            .commit()
    }
}
