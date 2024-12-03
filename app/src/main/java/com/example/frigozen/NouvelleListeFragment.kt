package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NouvelleListeFragment : Fragment(R.layout.fragment_nouvelle_liste) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var alimentsList: List<Aliment>
    private val selectedAliments = mutableListOf<Aliment>()
    private var listName: String? = null  // Variable pour stocker le nom de la liste


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérer le nom de la liste à partir des arguments
        listName = arguments?.getString("listName")

        // Liste d'aliment
        val alimentsList = listOf(
            Aliment("Pomme", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Melon", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Banane", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Carotte", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Tomate", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Poulet", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Saumon", R.drawable.bilan_nutritif_icon, "Poissons"),
            // Ajoutez d'autres aliments avec leur catégorie
        )

        // Grouper les aliments par catégorie
        val groupedAliments = alimentsList.groupBy { it.category }

        // Créer une liste mixte contenant des catégories et des aliments
        val items = mutableListOf<ListItem>()
        groupedAliments.forEach { (category, aliments) ->
            items.add(ListItem.Category(category)) // Ajoute l'en-tête de la catégorie
            items.addAll(aliments.map { ListItem.AlimentItem(it) }) // Ajoute les aliments sous la catégorie
        }



        // Initialisation de la RecyclerView avec la liste mixte (catégories + aliments)
        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AlimentAdapter(items) { aliment ->
            selectedAliments.add(aliment)
            Toast.makeText(requireContext(), "${aliment.name} ajouté à la liste!", Toast.LENGTH_SHORT).show()
        }





        // Bouton "V" en bas à droite clicker
        val btnOkList = view.findViewById<Button>(R.id.btnOkList)
        btnOkList.setOnClickListener {
            // Vous pouvez utiliser ce bouton pour une autre action (par exemple, finaliser la liste)
            showSelectedAliments()
        }
    }

    private fun showSelectedAliments() {
        // Vérifier si des aliments ont été sélectionnés
        if (selectedAliments.isEmpty()) {
            Toast.makeText(requireContext(), "Aucun aliment sélectionné.", Toast.LENGTH_SHORT).show()
            return
        }

        // Afficher les aliments sélectionnés dans un Toast
        val selectedNames = selectedAliments.joinToString(", ") { it.name }
        Toast.makeText(requireContext(), "Aliments ajoutés à $listName : $selectedNames", Toast.LENGTH_LONG).show()
        (activity as MainActivity).loadFragment(MesListesFragment()) // Appelle la méthode loadFragment de MainActivity pour changer de vue une fois que le bouton valider est selectionne


        // Vous pouvez maintenant naviguer vers une autre vue ou effectuer d'autres actions avec la liste des aliments sélectionnés
        // Par exemple, enregistrer la liste ou la passer à une autre activité/fragment
    }
}

