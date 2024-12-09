package com.example.frigozen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialiser la base de données
        databaseHelper = DatabaseHelper(requireContext())

        // Champs du layout
        val usernameField: EditText = view.findViewById(R.id.editTextUsername)
        val passwordField: EditText = view.findViewById(R.id.editTextPassword)
        val loginButton: Button = view.findViewById(R.id.buttonLogin)
        val createAccountButton: Button = view.findViewById(R.id.buttonCreateAccount)
        val forgotPasswordLink: TextView = view.findViewById(R.id.textForgotPassword)

        // Gestion du bouton "Se connecter"
        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            } else if (databaseHelper.isUserValid(username, password)) {
                Toast.makeText(context, "Connexion réussie.", Toast.LENGTH_SHORT).show()

            // Afficher l'utilisateur connecter dans l'application
                val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
                val editor = sharedPreferences.edit()
                editor.putString("username", username)  // Enregistrer le nom d'utilisateur
                editor.putBoolean("is_logged_in", true)  // Marquer l'utilisateur comme connecté
                editor.apply()

                // Naviguer vers le fragment BilanNutritifFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, BilanNutritifFragment())
                    .addToBackStack(null)  // Ajoutez à la pile arrière pour permettre la navigation arrière
                    .commit()
            } else {
                Toast.makeText(context, "Nom d'utilisateur ou mot de passe incorrect.", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigation vers la création de compte
        createAccountButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, AccountCreationFragment())
                .addToBackStack(null)
                .commit()
        }

        // Gestion du lien "Mot de passe oublié ?"
        forgotPasswordLink.setOnClickListener {
            Toast.makeText(context, "Fonction de récupération en cours de développement.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
