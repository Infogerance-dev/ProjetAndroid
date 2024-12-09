package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MesListesFragment : Fragment(R.layout.fragment_mes_listes) {

    private lateinit var dbHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dbHelper = DatabaseHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMesListes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Récupérer les listes de shopping pour l'utilisateur (ex: userId = 1)
        val userId = 1  // À adapter en fonction de l'utilisateur connecté
        val shoppingLists = dbHelper.getShoppingListsByUser(userId)

        val adapter = ShoppingListAdapter(shoppingLists) { shoppingList ->
            // On clique sur une liste de shopping
            val bundle = Bundle().apply {
                putInt("listId", shoppingList.id)
                putString("listName", shoppingList.name)
            }



            // Navigation vers le fragment de détails avec les aliments de la liste
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailListeFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
    }
}

