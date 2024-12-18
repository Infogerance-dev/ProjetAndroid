package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MesListesFragment : Fragment(R.layout.fragment_mes_listes) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var fragmentContainer: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMesListes)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        dbHelper = DatabaseHelper(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val userId = 1  // À adapter en fonction de l'utilisateur connecté
        val shoppingLists = dbHelper.getShoppingListsByUser(userId)

        val adapter = ShoppingListAdapter(shoppingLists) { shoppingList ->

            // Cacher RecyclerView et afficher FrameLayout
            recyclerView.visibility = View.GONE
            fragmentContainer.visibility = View.VISIBLE

            // Passer les informations de la liste à DetailListeFragment
            val bundle = Bundle().apply {
                putInt("listId", shoppingList.id)
                putString("listName", shoppingList.name)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailListeFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
    }

    // Cette méthode permet de réafficher RecyclerView et cacher le FrameLayout
    fun onBackPressed() {
        fragmentContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

    }
}
