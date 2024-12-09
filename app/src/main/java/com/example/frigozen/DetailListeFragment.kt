package com.example.frigozen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailListeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlimentAdapterListe
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_liste, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewListeDetails)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseHelper = DatabaseHelper(requireContext())

        adapter = AlimentAdapterListe(requireContext(), mutableListOf()) { selectedAliment ->
            Toast.makeText(requireContext(), "${selectedAliment.name} sélectionné", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        val listId = arguments?.getInt("listId", -1) ?: -1
        if (listId != -1) {
            loadAlimentsForList(listId)
        } else {
            Toast.makeText(requireContext(), "Erreur : ID de liste invalide", Toast.LENGTH_SHORT).show()
        }

        // Récupérer le nom de la liste et l'afficher
        val listName = arguments?.getString("listName", "") ?: ""
        val listNameTextView = view.findViewById<TextView>(R.id.listNameTextView)
        listNameTextView.text = listName

        // Configurer le bouton de retour en arrière
        val backButton: View = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Appel de la méthode dans le fragment parent pour réafficher le RecyclerView
            (parentFragment as? MesListesFragment)?.onBackPressed()

            // Retourner dans la pile des fragments
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadAlimentsForList(listId: Int) {
        try {
            val items = databaseHelper.getItemsByListId(listId)
            if (items.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Aucun aliment trouvé pour cette liste", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateItems(items)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erreur lors du chargement des aliments", Toast.LENGTH_SHORT).show()
        }
    }
}
