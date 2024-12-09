package com.example.frigozen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        // Récupérer les SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
        val username = sharedPreferences.getString("username", null)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        // Récupérer les champs de saisie et le bouton
        val usernameField: EditText = view.findViewById(R.id.editTextUsername)
        val emailField: EditText = view.findViewById(R.id.editTextEmail)
        val passwordField: EditText = view.findViewById(R.id.editTextPassword)
        val confirmPasswordField: EditText = view.findViewById(R.id.editTextConfirmPassword)
        val saveButton: Button = view.findViewById(R.id.buttonSavePassword)

        // Ajouter les vues supplémentaires pour afficher les infos utilisateur
        val accountInfoTextView: TextView = view.findViewById(R.id.textViewAccountInfo)
        val logoutButton: Button = view.findViewById(R.id.buttonLogout)

        if (isLoggedIn && username != null) {
            // L'utilisateur est connecté, afficher ses informations
            accountInfoTextView.visibility = View.VISIBLE
            logoutButton.visibility = View.VISIBLE
            accountInfoTextView.text = "Bienvenue, $username !"

            // Cacher les champs de création de compte
            usernameField.visibility = View.GONE
            emailField.visibility = View.GONE
            passwordField.visibility = View.GONE
            confirmPasswordField.visibility = View.GONE
            saveButton.visibility = View.GONE

            // Ajouter l'action pour le bouton de déconnexion
            logoutButton.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.clear() // Supprimer les informations de connexion
                editor.apply()

                // Réafficher les champs de création de compte
                usernameField.visibility = View.VISIBLE
                emailField.visibility = View.VISIBLE
                passwordField.visibility = View.VISIBLE
                confirmPasswordField.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE

                // Cacher les infos utilisateur et le bouton de déconnexion
                accountInfoTextView.visibility = View.GONE
                logoutButton.visibility = View.GONE

                Toast.makeText(context, "Déconnexion réussie.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // L'utilisateur n'est pas connecté, permettre la création d'un compte
            accountInfoTextView.visibility = View.GONE
            logoutButton.visibility = View.GONE

            // Ajouter le listener pour le bouton "Enregistrer"
            saveButton.setOnClickListener {
                val usernameInput = usernameField.text.toString().trim()
                val emailInput = emailField.text.toString().trim()
                val passwordInput = passwordField.text.toString()
                val confirmPasswordInput = confirmPasswordField.text.toString()

                // Validation des champs
                if (usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                    Toast.makeText(context, "Veuillez remplir toutes les informations requises.", Toast.LENGTH_SHORT).show()
                } else if (passwordInput != confirmPasswordInput) {
                    Toast.makeText(context, "Les mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show()
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    Toast.makeText(context, "Veuillez entrer une adresse email valide.", Toast.LENGTH_SHORT).show()
                } else if (databaseHelper.isEmailExists(emailInput)) {
                    Toast.makeText(context, "Cet email est déjà utilisé.", Toast.LENGTH_SHORT).show()
                } else {
                    // Enregistrement dans SQLite
                    val userId = databaseHelper.insertUser(usernameInput, emailInput, passwordInput)
                    if (userId > 0) {
                        Toast.makeText(context, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()

                        // Sauvegarder l'utilisateur comme connecté
                        val editor = sharedPreferences.edit()
                        editor.putString("username", usernameInput)
                        editor.putBoolean("is_logged_in", true)
                        editor.apply()

                        // Afficher les informations de l'utilisateur
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment, BilanNutritifFragment())
                            .commit()
                    } else {
                        Toast.makeText(context, "Erreur lors de la création du compte. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
                    }
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
