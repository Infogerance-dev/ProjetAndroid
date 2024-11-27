package com.example.frigozen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog

class BilanNutritifFragment : Fragment(R.layout.fragment_bilan_nutritif) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPoids: EditText = view.findViewById(R.id.etPoids)
        val etTaille: EditText = view.findViewById(R.id.etTaille)
        val btnCalculer: Button = view.findViewById(R.id.btnCalculer)
        val tvIMC: TextView = view.findViewById(R.id.tvIMC)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)

        btnCalculer.setOnClickListener {
            val poids = etPoids.text.toString().toFloatOrNull()
            val taille = etTaille.text.toString().toFloatOrNull()

            if (poids != null && taille != null) {
                val imc = poids / (taille * taille)
                tvIMC.text = "IMC: %.2f".format(imc)

                // Calcul des calories
                var calories = poids * 23

                // Popup pour demander l'activité physique
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
                    tvCalories.text = "Calories recommandées: %.0f".format(calories)
                }
                builder.show()
            } else {
                tvIMC.text = "Veuillez entrer un poids et une taille valides."
                tvCalories.text = ""
            }
        }
    }
}
