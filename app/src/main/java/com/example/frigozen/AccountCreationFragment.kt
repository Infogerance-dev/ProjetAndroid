package com.example.frigozen

import android.content.Context
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
import android.util.Log


class AccountCreationFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private var isUserLoggedIn: Boolean = false // Indicateur de connexion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_creation, container, false)

        // Initialiser l'instance de DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Récupérer les vues
        val backButton: Button = view.findViewById(R.id.buttonBackToLogin)
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val accountInfoTextView: TextView = view.findViewById(R.id.textViewAccountInfo)
        val logoutButton: Button = view.findViewById(R.id.buttonLogout)
        val usernameField: EditText = view.findViewById(R.id.editTextUsername)
        val emailField: EditText = view.findViewById(R.id.editTextEmail)
        val passwordField: EditText = view.findViewById(R.id.editTextPassword)
        val confirmPasswordField: EditText = view.findViewById(R.id.editTextConfirmPassword)
        val saveButton: Button = view.findViewById(R.id.buttonSavePassword)

        // Gérer le clic sur le bouton "Retour"
        backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .commit()
        }

        // Vérifier si l'utilisateur est connecté
        isUserLoggedIn = checkUserLoggedIn()

        if (isUserLoggedIn) {
            Log.d("AccountCreationFragment", "L'utilisateur est connecté. Configuration de l'état connecté.")
            // Afficher les informations de l'utilisateur connecté
            titleTextView.visibility = View.GONE
            usernameField.visibility = View.GONE
            emailField.visibility = View.GONE
            passwordField.visibility = View.GONE
            confirmPasswordField.visibility = View.GONE
            saveButton.visibility = View.GONE

            accountInfoTextView.visibility = View.VISIBLE
            logoutButton.visibility = View.VISIBLE

            // Récupérer les informations de l'utilisateur connecté
            val user = databaseHelper.getCurrentUser()
            accountInfoTextView.text = "Bienvenue, ${user?.username} !"

            // Gestion du bouton de déconnexion
            logoutButton.setOnClickListener {
                databaseHelper.logoutUser()
                Toast.makeText(context, "Déconnexion réussie.", Toast.LENGTH_SHORT).show()

                // Recharger le fragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, LoginFragment())
                    .commit()
            }
        } else {
            Log.d("AccountCreationFragment", "L'utilisateur n'est pas connecté. Configuration de l'état de création de compte.")
            // Afficher les éléments pour la création de compte
            titleTextView.visibility = View.VISIBLE
            usernameField.visibility = View.VISIBLE
            emailField.visibility = View.VISIBLE
            passwordField.visibility = View.VISIBLE
            confirmPasswordField.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE

            accountInfoTextView.visibility = View.GONE
            logoutButton.visibility = View.GONE

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

    private fun checkUserLoggedIn(): Boolean {
        val user = databaseHelper.getCurrentUser()
        Log.d("AccountCreationFragment", "Récupération de l'utilisateur")
        return if (user != null) {
            val username = user.username  // récupère le nom d'utilisateur
            val retrievedUser = databaseHelper.getUserByUsername(username) // Utiliser le nom d'utilisateur pour récupérer l'utilisateur
            Log.d("AccountCreationFragment", "Vérification de l'état de connexion : ${retrievedUser != null}")
            retrievedUser != null
        } else {
            Log.d("AccountCreationFragment", "Utilisateur non connecté}")
            false
        }
    }


}

