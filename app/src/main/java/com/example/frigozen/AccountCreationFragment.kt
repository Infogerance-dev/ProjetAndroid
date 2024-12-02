package com.example.frigozen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountCreationFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_creation, container, false)

        // Initialiser l'instance de DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Récupérer les champs de saisie et le bouton
        val usernameField: EditText = view.findViewById(R.id.editTextUsername)
        val emailField: EditText = view.findViewById(R.id.editTextEmail)
        val passwordField: EditText = view.findViewById(R.id.editTextPassword)
        val confirmPasswordField: EditText = view.findViewById(R.id.editTextConfirmPassword)
        val saveButton: Button = view.findViewById(R.id.buttonSavePassword)

        // Ajouter le listener pour le bouton "Enregistrer"
        saveButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            // Validation des champs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Veuillez remplir toutes les informations requises.", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(context, "Les mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Veuillez entrer une adresse email valide.", Toast.LENGTH_SHORT).show()
            } else if (databaseHelper.isEmailExists(email)) {
                Toast.makeText(context, "Cet email est déjà utilisé.", Toast.LENGTH_SHORT).show()
            } else {
                // Enregistrement dans SQLite
                val userId = databaseHelper.insertUser(username, email, password)
                if (userId > 0) {
                    Toast.makeText(context, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()

                    // Navigation vers un autre écran
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, BilanNutritifFragment())
                        .commit()
                } else {
                    Toast.makeText(context, "Erreur lors de la création du compte. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.VISIBLE
    }
}

