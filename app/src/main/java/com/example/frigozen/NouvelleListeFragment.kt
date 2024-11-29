package com.example.frigozen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NouvelleListeFragment : Fragment(R.layout.fragment_nouvelle_liste) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var alimentsList: List<Aliment>
    private val selectedAliments = mutableListOf<Aliment>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exemple de liste d'aliments (remplacez par vos données réelles)
        alimentsList = List(50) { index ->
            Aliment("Aliment ${index + 1}", "https://example.com/images/food_${index + 1}.jpg")
        }

        // Initialisation de la RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AlimentsAdapter(alimentsList) { aliment ->
            // Action lorsque l'utilisateur ajoute un aliment à la liste
            selectedAliments.add(aliment)
            Toast.makeText(requireContext(), "${aliment.name} ajouté à la liste!", Toast.LENGTH_SHORT).show()
        }

        // Bouton "+" en bas à droite
        val btnAddList = view.findViewById<Button>(R.id.btnAddList)
        btnAddList.setOnClickListener {
            // Vous pouvez utiliser ce bouton pour une autre action (par exemple, finaliser la liste)
            showSelectedAliments()
        }
    }

    private fun showSelectedAliments() {
        // Afficher les aliments sélectionnés dans un Toast
        val selectedNames = selectedAliments.joinToString(", ") { it.name }
        Toast.makeText(requireContext(), "Aliments sélectionnés: $selectedNames", Toast.LENGTH_LONG).show()

        // Vous pouvez maintenant naviguer vers une autre vue ou effectuer d'autres actions avec la liste des aliments sélectionnés
    }
}

