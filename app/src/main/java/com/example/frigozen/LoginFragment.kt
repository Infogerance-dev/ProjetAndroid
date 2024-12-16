package com.example.frigozen

import android.content.SharedPreferences
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

class LoginFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Cacher le BottomNavigationView
        (activity as? MainActivity)?.setBottomNavigationVisibility(false)

        // Initialiser la base de données
        databaseHelper = DatabaseHelper(requireContext())

        // Initialiser SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)

        // Masquer la barre de navigation système
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Initialiser les vues
        val usernameField: EditText? = view.findViewById(R.id.editTextUsername)
        val passwordField: EditText? = view.findViewById(R.id.editTextPassword)
        val loginButton: Button? = view.findViewById(R.id.buttonLogin)
        val createAccountButton: Button? = view.findViewById(R.id.buttonCreateAccount)
        val forgotPasswordLink: TextView? = view.findViewById(R.id.textForgotPassword)


        // Ajouter un listener au bouton de connexion
        loginButton?.setOnClickListener {
            val username = usernameField?.text.toString().trim()
            val password = passwordField?.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            } else if (databaseHelper.isUserValid(username, password)) {
                Toast.makeText(context, "Connexion réussie.", Toast.LENGTH_SHORT).show()

                // Enregistrer les informations de l'utilisateur dans SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putBoolean("is_logged_in", true)
                editor.putInt("id", id)
                editor.apply()

                // Cacher le BottomNavigationView
                (activity as? MainActivity)?.setBottomNavigationVisibility(true)

                // Naviguer vers le fragment BilanNutritifFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, BilanNutritifFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(context, "Nom d'utilisateur ou mot de passe incorrect.", Toast.LENGTH_SHORT).show()
            }
        }

        // Ajouter un listener au bouton de création de compte
        createAccountButton?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, AccountCreationFragment())
                .addToBackStack(null)
                .commit()
        }

        // Ajouter un listener au lien "Mot de passe oublié"
        forgotPasswordLink?.setOnClickListener {
            Toast.makeText(context, "Fonction de récupération en cours de développement.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
