package com.example.frigozen

import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.frigozen.BilanNutritifFragment
import com.example.frigozen.MesListesFragment
import com.example.frigozen.AccountCreationFragment
import com.example.frigozen.NouvelleListeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.frigozen.R

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vérification de l'Intent pour la demande de permission
        if (intent.getBooleanExtra("request_permission", false)) {
            // Vérifier si la permission est déjà accordée
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                != PackageManager.PERMISSION_GRANTED) {

                // Demander la permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                    REQUEST_CODE_PERMISSION
                )
            }
        }

        // Initialisation de la barre de navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configurer la navigation pour les fragments principaux
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meslistes -> {
                    loadFragment(MesListesFragment())  // Remplacez par le fragment Home
                    true
                }
                R.id.nav_nouvelleliste -> {
                    // Remplacez par le fragment Search
                    loadFragment(NouvelleListeFragment())

                    // Afficher la boîte de dialogue pour saisir le nom de la nouvelle liste
                    showNewListDialog()

                    true
                }
                R.id.nav_compte -> {
                    loadFragment(AccountCreationFragment())  // Remplacez par le fragment Profile
                    true
                }
                R.id.nav_bilannutritif -> {
                    loadFragment(BilanNutritifFragment())  // Affiche le BilanNutritifFragment
                    true
                }
                else -> false
            }
        }

        // Charger le fragment par défaut au démarrage
        if (savedInstanceState == null) {
            loadFragment(BilanNutritifFragment())  // Le fragment BilanNutritif s'affichera au démarrage
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .commit()
        }
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Gestion de la réponse à la demande de permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, continuer normalement
                Toast.makeText(this, "Permission pour les notifications accordée", Toast.LENGTH_SHORT).show()
            } else {
                // Permission refusée, vous pouvez afficher un message ou une action
                Toast.makeText(this, "Permission requise pour afficher les notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showNewListDialog() {
        // Créer un objet AlertDialog pour demander le nom de la nouvelle liste
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nom de la nouvelle liste")

        // Créer un EditText pour entrer le nom de la liste
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val listName = input.text.toString()
            if (listName.isNotEmpty()) {
                // Passer le nom de la liste au fragment NouvelleListeFragment
                val fragment = NouvelleListeFragment()
                val bundle = Bundle()
                bundle.putString("listName", listName)
                fragment.arguments = bundle


                // Utilisez le nom de la liste (par exemple, pour enregistrer la liste ou l'afficher)
                Toast.makeText(this, "Nouvelle liste créée : $listName", Toast.LENGTH_SHORT).show()

                // Charger le fragment NouvelleListeFragment
                loadFragment(fragment)

            } else {
                Toast.makeText(this, "Le nom de la liste ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()  // Affiche la boîte de dialogue
    }
    // Méthode pour masquer la barre de navigation et la barre de statut
    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }




    // Navigation après une connexion réussie
    fun navigateToDefaultFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, BilanNutritifFragment())
            .commit()
    }
}

