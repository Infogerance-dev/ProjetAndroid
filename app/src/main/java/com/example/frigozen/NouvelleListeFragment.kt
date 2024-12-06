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
            Aliment("Pomme", R.drawable.bilan_nutritif_icon, "Fruits", 52),
            Aliment("Melon", R.drawable.bilan_nutritif_icon, "Fruits", 34),
            Aliment("Banane", R.drawable.bilan_nutritif_icon, "Fruits", 89),
            // Ajoutez le reste de vos aliments ici
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
            grammesRadioButton.isChecked = true // Par défaut, on choisit les grammes

            val kilosRadioButton = RadioButton(context)
            kilosRadioButton.text = "Kilogrammes"

            radioGroup.addView(grammesRadioButton)
            radioGroup.addView(kilosRadioButton)

            layout.addView(radioGroup)

            builder.setView(layout)

            builder.setPositiveButton("Ajouter") { _, _ ->
                val quantityText = quantityInput.text.toString()
                if (quantityText.isNotEmpty() && quantityText.toIntOrNull() != null) {
                    val quantity = quantityText.toInt()
                    val unit = if (radioGroup.checkedRadioButtonId == grammesRadioButton.id) {
                        "Gramme"
                    } else {
                        "Kilogramme"
                    }

                    if (quantity > 0) {
                        selectedAliments.add(aliment.copy(quantity = quantity))
                        Toast.makeText(context, "Ajouté: ${aliment.name}, Quantité: $quantity $unit", Toast.LENGTH_SHORT).show()
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
