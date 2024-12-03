package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class BilanNutritifFragment : Fragment(R.layout.fragment_bilan_nutritif) {

    private var maxCalories = 0 // Stocke le nombre de calories recommandées

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPoids: EditText = view.findViewById(R.id.etPoids)
        val etTaille: EditText = view.findViewById(R.id.etTaille)
        val btnCalculer: Button = view.findViewById(R.id.btnCalculer)
        val tvIMC: TextView = view.findViewById(R.id.tvIMC)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalories)
        val btnCaloriesAction: Button = view.findViewById(R.id.btnCaloriesAction)
        val groupResult: LinearLayout = view.findViewById(R.id.groupResult) // Conteneur des résultats

        // Cache les résultats au départ
        groupResult.visibility = View.GONE

        btnCalculer.setOnClickListener {
            val poids = etPoids.text.toString().toFloatOrNull()
            val taille = etTaille.text.toString().toFloatOrNull()

            if (poids != null && taille != null) {
                val imc = poids / (taille * taille)
                tvIMC.text = "IMC: %.2f".format(imc)

                var calories = poids * 23

                // Popup pour le niveau d'activité
                val options = arrayOf("Sédentaire", "Modérément actif", "Très actif")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Niveau d'activité")
                builder.setItems(options) { _, which ->
                    val facteur = when (which) {
                        0 -> 1.2f
                        1 -> 1.5f
                        else -> 1.8f
                    }
                    calories *= facteur
                    if (imc < 18.5) calories += 400 else if (imc > 25) calories -= 400

                    // Mise à jour des calories recommandées
                    maxCalories = calories.toInt()
                    tvCalories.text = "Calories recommandées: $maxCalories"

                    // Configurer le maximum de la ProgressBar
                    progressBar.max = maxCalories
                    progressBar.progress = 0

                    // Afficher les résultats
                    groupResult.visibility = View.VISIBLE
                }
                builder.show()
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer un poids et une taille valides.", Toast.LENGTH_SHORT).show()
                groupResult.visibility = View.GONE
            }
        }

        // Bouton pour ouvrir "Mes listes" (fonctionnalité à venir)
        btnCaloriesAction.setOnClickListener {
            Toast.makeText(requireContext(), "Ouvrir 'Mes listes' pour ajouter des aliments.", Toast.LENGTH_SHORT).show()
        }
    }
}
