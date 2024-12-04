package com.example.frigozen

import android.os.Bundle
import android.util.Log
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

        val alimentsList = listOf(
            Aliment("Pomme", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Melon", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Banane", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Poire", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Orange", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Fraise", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Raisin", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Abricot", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Pêche", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Ananas", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Kiwi", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Mangue", R.drawable.bilan_nutritif_icon, "Fruits"),
            Aliment("Carotte", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Tomate", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Courgette", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Brocoli", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Épinard", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Poivron", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Chou-fleur", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Concombre", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Laitue", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Oignon", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Ail", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Haricot vert", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Chou de Bruxelles", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Céleri", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Poulet", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Boeuf", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Porc", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Agneau", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Dinde", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Canard", R.drawable.bilan_nutritif_icon, "Viandes"),
            Aliment("Saumon", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Thon", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Maquereau", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Truite", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Morue", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Sardine", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Crevette", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Homard", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Crabe", R.drawable.bilan_nutritif_icon, "Poissons"),
            Aliment("Tofu", R.drawable.bilan_nutritif_icon, "Protéines végétales"),
            Aliment("Tempeh", R.drawable.bilan_nutritif_icon, "Protéines végétales"),
            Aliment("Lentilles", R.drawable.bilan_nutritif_icon, "Légumineuses"),
            Aliment("Pois chiches", R.drawable.bilan_nutritif_icon, "Légumineuses"),
            Aliment("Haricots rouges", R.drawable.bilan_nutritif_icon, "Légumineuses"),
            Aliment("Edamame", R.drawable.bilan_nutritif_icon, "Légumineuses"),
            Aliment("Chia", R.drawable.bilan_nutritif_icon, "Graines"),
            Aliment("Lin", R.drawable.bilan_nutritif_icon, "Graines"),
            Aliment("Tournesol", R.drawable.bilan_nutritif_icon, "Graines"),
            Aliment("Courge", R.drawable.bilan_nutritif_icon, "Légumes"),
            Aliment("Amandes", R.drawable.bilan_nutritif_icon, "Fruits secs"),
            Aliment("Noix", R.drawable.bilan_nutritif_icon, "Fruits secs"),
            Aliment("Pistaches", R.drawable.bilan_nutritif_icon, "Fruits secs"),
            Aliment("Noisettes", R.drawable.bilan_nutritif_icon, "Fruits secs")
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


        // Appelle la méthode loadFragment de MainActivity pour changer de vue une fois que le bouton valider est selectionne
        (activity as MainActivity).loadFragment(MesListesFragment())


        // Vous pouvez maintenant naviguer vers une autre vue ou effectuer d'autres actions avec la liste des aliments sélectionnés
        // Par exemple, enregistrer la liste ou la passer à une autre activité/fragment
    }
}

