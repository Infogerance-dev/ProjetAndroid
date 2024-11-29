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

        // Liste d'aliment
        val alimentsList = listOf(
            Aliment("Pomme", R.drawable.bilan_nutritif_icon),
            Aliment("Melon", R.drawable.bilan_nutritif_icon),
            Aliment("Banane", R.drawable.bilan_nutritif_icon),
            Aliment("Carotte", R.drawable.bilan_nutritif_icon),
            Aliment("Tomate", R.drawable.bilan_nutritif_icon),
            Aliment("Brocoli", R.drawable.bilan_nutritif_icon),
            Aliment("Riz", R.drawable.bilan_nutritif_icon),
            Aliment("Poulet", R.drawable.bilan_nutritif_icon),
            Aliment("Saumon", R.drawable.bilan_nutritif_icon),
            Aliment("Lait", R.drawable.bilan_nutritif_icon),
            Aliment("Fromage", R.drawable.bilan_nutritif_icon),
            Aliment("Pain", R.drawable.bilan_nutritif_icon),
            Aliment("Œuf", R.drawable.bilan_nutritif_icon),
            Aliment("Avocat", R.drawable.bilan_nutritif_icon),
            Aliment("Salade", R.drawable.bilan_nutritif_icon),
            Aliment("Poivron", R.drawable.bilan_nutritif_icon),
            Aliment("Courgette", R.drawable.bilan_nutritif_icon),
            Aliment("Épinards", R.drawable.bilan_nutritif_icon),
            Aliment("Chou-fleur", R.drawable.bilan_nutritif_icon),
            Aliment("Pêche", R.drawable.bilan_nutritif_icon),
            Aliment("Mûre", R.drawable.bilan_nutritif_icon),
            Aliment("Mangue", R.drawable.bilan_nutritif_icon),
            Aliment("Kiwi", R.drawable.bilan_nutritif_icon),
            Aliment("Melon", R.drawable.bilan_nutritif_icon),
            Aliment("Amandes", R.drawable.bilan_nutritif_icon),
            Aliment("Noix de cajou", R.drawable.bilan_nutritif_icon),
            Aliment("Yaourt", R.drawable.bilan_nutritif_icon),
            Aliment("Tofu", R.drawable.bilan_nutritif_icon),
            Aliment("Chocolat", R.drawable.bilan_nutritif_icon),
            Aliment("Café", R.drawable.bilan_nutritif_icon),
            Aliment("Thé", R.drawable.bilan_nutritif_icon)
        )



        // Initialisation de la RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AlimentsAdapter(alimentsList) { aliment ->
            // Action lorsque l'utilisateur ajoute un aliment à la liste
            selectedAliments.add(aliment)
            Toast.makeText(requireContext(), "${aliment.name} ajouté à la liste!", Toast.LENGTH_SHORT).show()
        }



        // Bouton "V" en bas à droite
        val btnOkList = view.findViewById<Button>(R.id.btnOkList)
        btnOkList.setOnClickListener {
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

