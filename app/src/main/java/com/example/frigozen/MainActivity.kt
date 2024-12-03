package com.example.frigozen

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.frigozen.BilanNutritifFragment
import com.example.frigozen.MesListesFragment
import com.example.frigozen.MonCompteFragment
import com.example.frigozen.NouvelleListeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.frigozen.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meslistes -> {
                    loadFragment(MesListesFragment())  // Remplacez par le fragment Home
                    true
                }
                R.id.nav_nouvelleliste -> {
                    // Charger le fragment NouvelleListeFragment
                    loadFragment(NouvelleListeFragment())

                    // Afficher la boîte de dialogue pour saisir le nom de la nouvelle liste
                    showNewListDialog()

                    true
                }
                R.id.nav_compte -> {
                    loadFragment(MonCompteFragment())  // Remplacez par le fragment Profile
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
            loadFragment(BilanNutritifFragment())  // Le fragment Home s'affichera au démarrage
        }
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
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
}
