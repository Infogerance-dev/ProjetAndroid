package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class BilanNutritifFragment : Fragment(R.layout.fragment_bilan_nutritif) {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext()) // Initialiser la base

        val etPoids: EditText = view.findViewById(R.id.etPoids)
        val etTaille: EditText = view.findViewById(R.id.etTaille)
        val btnCalculer: Button = view.findViewById(R.id.btnCalculer)
        val tvIMC: TextView = view.findViewById(R.id.tvIMC)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalories)

        // Charger les données utilisateur (IMC et calories)
        val currentUserEmail = "user@example.com" // À remplacer par l'email de l'utilisateur connecté
        val user = databaseHelper.getUser(currentUserEmail)

        if (user != null && user.imc != null && user.calories != null) {
            // Si les données existent, les afficher
            tvIMC.visibility = View.VISIBLE
            tvCalories.visibility = View.VISIBLE
            etPoids.visibility = View.GONE
            etTaille.visibility = View.GONE
            btnCalculer.visibility = View.GONE

            tvIMC.text = "IMC: %.2f".format(user.imc)
            tvCalories.text = "Calories recommandées: %.0f".format(user.calories)
            progressBar.max = user.calories.toInt()
        } else {
            // Affichage par défaut (pas de données)
            tvIMC.visibility = View.GONE
            tvCalories.visibility = View.GONE
            etPoids.visibility = View.VISIBLE
            etTaille.visibility = View.VISIBLE
            btnCalculer.visibility = View.VISIBLE
        }

        // Logique du bouton calculer
        btnCalculer.setOnClickListener {
            val poids = etPoids.text.toString().toFloatOrNull()
            val taille = etTaille.text.toString().toFloatOrNull()

            if (poids != null && taille != null) {
                val imc = poids / (taille * taille)
                var calories = poids * 23

                // Afficher un popup pour le niveau d'activité et ajuster les calories
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
                    if (imc < 18.5) {
                        calories += 400
                    } else if (imc > 25) {
                        calories -= 400
                    }

                    // Enregistrer les résultats dans la base de données
                    databaseHelper.updateHealthData(currentUserEmail, imc, calories)

                    // Mettre à jour l'interface
                    tvIMC.text = "IMC: %.2f".format(imc)
                    tvCalories.text = "Calories recommandées: %.0f".format(calories)
                    progressBar.max = calories.toInt()

                    tvIMC.visibility = View.VISIBLE
                    tvCalories.visibility = View.VISIBLE
                    etPoids.visibility = View.GONE
                    etTaille.visibility = View.GONE
                    btnCalculer.visibility = View.GONE
                }
                builder.show()
            } else {
                tvIMC.text = "Veuillez entrer un poids et une taille valides."
            }
        }
    }
}
