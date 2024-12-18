package com.example.frigozen

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frigozen.ListItem

class NouvelleListeFragment : Fragment(R.layout.fragment_nouvelle_liste) {

    private lateinit var recyclerView: RecyclerView
    private val selectedAliments = mutableListOf<Aliment>()
    private var listName: String? = null  // Variable pour stocker le nom de la liste

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseHelper = DatabaseHelper(requireContext())
        val itemNames = selectedAliments.map { it.name }

        val email = "testuser@example.com"
        val user = databaseHelper.getUser(email)
        val userId = user?.id ?: run {
            val username = "testuser"
            val password = "password123"
            databaseHelper.insertUser(username, email, password)
        }

        // Récupérer le nom de la liste à partir des arguments
        listName = arguments?.getString("listName")

        val alimentsList = listOf(
            Aliment("Pomme", R.drawable._12695, "Fruits", 52),
            Aliment("Melon", R.drawable.melon1, "Fruits", 34),
            Aliment("Poire", R.drawable.poire, "Fruits", 57),
            Aliment("Orange", R.drawable.orange, "Fruits", 47),
            Aliment("Fraise", R.drawable.fraise, "Fruits", 32),
            Aliment("Raisin", R.drawable.raisin, "Fruits", 69),
            Aliment("Pêche", R.drawable.abricot, "Fruits", 39),
            Aliment("Ananas", R.drawable.ananas, "Fruits", 50),
            Aliment("Mangue", R.drawable.mangue, "Fruits", 60),
            Aliment("Carotte", R.drawable.carotte, "Légumes", 41),
            Aliment("Tomate", R.drawable.tomate, "Légumes", 18),
            Aliment("Courgette", R.drawable.bilan_nutritif_icon, "Légumes", 17),
            Aliment("Brocoli", R.drawable.bilan_nutritif_icon, "Légumes", 34),
            Aliment("Ail", R.drawable.bilan_nutritif_icon, "Légumes", 149),
            Aliment("Chou de Bruxelles", R.drawable.bilan_nutritif_icon, "Légumes", 43),
            Aliment("Poulet", R.drawable.bilan_nutritif_icon, "Viandes", 165),
            Aliment("Boeuf", R.drawable.bilan_nutritif_icon, "Viandes", 250),
            Aliment("Porc", R.drawable.bilan_nutritif_icon, "Viandes", 242),
            Aliment("Agneau", R.drawable.bilan_nutritif_icon, "Viandes", 294),
            Aliment("Dinde", R.drawable.bilan_nutritif_icon, "Viandes", 135),
            Aliment("Canard", R.drawable.bilan_nutritif_icon, "Viandes", 337),
            Aliment("Saumon", R.drawable.saumon, "Poissons", 208),
            Aliment("Thon", R.drawable.thon, "Poissons", 132),
            Aliment("Maquereau", R.drawable.bilan_nutritif_icon, "Poissons", 305),
            Aliment("Truite", R.drawable.bilan_nutritif_icon, "Poissons", 148),
            Aliment("Morue", R.drawable.bilan_nutritif_icon, "Poissons", 82),
            Aliment("Sardine", R.drawable.bilan_nutritif_icon, "Poissons", 208),
            Aliment("Crevette", R.drawable.bilan_nutritif_icon, "Poissons", 99),
            Aliment("Homard", R.drawable.bilan_nutritif_icon, "Poissons", 77),
            Aliment("Crabe", R.drawable.bilan_nutritif_icon, "Poissons", 97),
            Aliment("Tofu", R.drawable.bilan_nutritif_icon, "Protéines végétales", 76),
            Aliment("Tempeh", R.drawable.bilan_nutritif_icon, "Protéines végétales", 193),
            Aliment("Lentilles", R.drawable.bilan_nutritif_icon, "Légumineuses", 116),
            Aliment("Pois chiches", R.drawable.bilan_nutritif_icon, "Légumineuses", 164),
            Aliment("Haricots rouges", R.drawable.bilan_nutritif_icon, "Légumineuses", 127),
            Aliment("Edamame", R.drawable.bilan_nutritif_icon, "Légumineuses", 121),
            Aliment("Chia", R.drawable.bilan_nutritif_icon, "Graines", 486),
            Aliment("Lin", R.drawable.bilan_nutritif_icon, "Graines", 534),
            Aliment("Tournesol", R.drawable.bilan_nutritif_icon, "Graines", 584),
            Aliment("Courge", R.drawable.bilan_nutritif_icon, "Légumes", 26),
            Aliment("Amandes", R.drawable.bilan_nutritif_icon, "Fruits secs", 579),
            Aliment("Noix", R.drawable.bilan_nutritif_icon, "Fruits secs", 654),
            Aliment("Pistaches", R.drawable.bilan_nutritif_icon, "Fruits secs", 562),
            Aliment("Noisettes", R.drawable.bilan_nutritif_icon, "Fruits secs", 628)
        )

        fun showAddToListDialog(context: Context, aliment: Aliment) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Quantité à ajouter")

            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            // EditText pour la quantité
            val quantityInput = EditText(context)
            quantityInput.inputType = InputType.TYPE_CLASS_NUMBER
            quantityInput.hint = "Quantité (entier)"
            layout.addView(quantityInput)

            val radioGroup = RadioGroup(context)
            radioGroup.orientation = RadioGroup.HORIZONTAL

            val grammesRadioButton = RadioButton(context)
            grammesRadioButton.text = "Grammes"
            grammesRadioButton.id = View.generateViewId()
            grammesRadioButton.isChecked = true // Par défaut, on choisit les grammes

            val kilosRadioButton = RadioButton(context)
            kilosRadioButton.text = "Kilogrammes"
            kilosRadioButton.id = View.generateViewId()

            radioGroup.addView(grammesRadioButton)
            radioGroup.addView(kilosRadioButton)

            layout.addView(radioGroup)

            builder.setView(layout)

            builder.setPositiveButton("Ajouter") { _, _ ->
                val quantityText = quantityInput.text.toString()
                if (quantityText.isNotEmpty() && quantityText.toIntOrNull() != null) {
                    var quantity = quantityText.toInt()
                    val selectedUnit = when (radioGroup.checkedRadioButtonId) {
                        grammesRadioButton.id -> "Grammes"
                        kilosRadioButton.id -> {
                            // Convertir les kilogrammes en grammes
                            quantity *= 1000
                            "Kilogrammes (converti en grammes)"
                        }
                        else -> {
                            Toast.makeText(context, "Veuillez sélectionner une unité.", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                    }

                    if (quantity > 0) {
                        // Calculer les calories ajustées
                        val adjustedCalories = (quantity / 100.0 * aliment.calories).toInt()

                        // Ajouter l'aliment avec les calories ajustées à la liste
                        selectedAliments.add(aliment.copy(quantity = quantity, calories = adjustedCalories))

                        Toast.makeText(
                            context,
                            "Ajouté: ${aliment.name}, Quantité: $quantity g, Calories: $adjustedCalories kcal",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "La quantité doit être positive", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Veuillez entrer une quantité valide", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Annuler") { dialog, _ -> dialog.cancel() }
            builder.show()
        }



        // Grouper les aliments par catégorie
        val groupedAliments = alimentsList.groupBy { it.category }

        // Créer une liste mixte contenant des catégories et des aliments
        val items = mutableListOf<ListItem>()
        groupedAliments.forEach { (category, aliments) ->
            items.add(ListItem.Category(category))
            items.addAll(aliments.map { ListItem.AlimentItem(it) })
        }

        // Initialisation de la RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AlimentAdapter(requireContext(), items) { aliment ->
            showAddToListDialog(requireContext(), aliment)
        }

        // Bouton "V" en bas à droite
        val btnOkList = view.findViewById<Button>(R.id.btnOkList)
        btnOkList.setOnClickListener {
            if (listName.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Veuillez entrer un nom pour la liste.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedAliments.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez sélectionner au moins un aliment.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = 1 // ID utilisateur temporaire
            val listAliments: List<ListAliment> = selectedAliments.map { aliment ->
                ListAliment(aliment.name, aliment.quantity, aliment.calories)
            }

            val listId = databaseHelper.insertShoppingList(userId, listName!!, listAliments)

            if (listId != -1L) {
                Toast.makeText(requireContext(), "Liste $listName créée avec succès !", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).loadFragment(MesListesFragment()) // Naviguer vers les listes
            } else {
                Toast.makeText(requireContext(), "Erreur lors de la création de la liste.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSelectedAliments() {
        if (selectedAliments.isEmpty()) {
            Toast.makeText(requireContext(), "Aucun aliment sélectionné.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedNames = selectedAliments.joinToString(", ") { it.name }
        Toast.makeText(requireContext(), "Aliments ajoutés à $listName : $selectedNames", Toast.LENGTH_LONG).show()

        (activity as MainActivity).loadFragment(MesListesFragment())
    }
}
