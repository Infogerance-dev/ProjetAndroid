package com.example.frigozen

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.frigozen.BilanNutritifFragment
import com.example.frigozen.MesListesFragment
import com.example.frigozen.MonCompteFragment
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
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meslistes -> {
                    loadFragment(MesListesFragment())  // Remplacez par le fragment MesListes
                    true
                }
                R.id.nav_nouvelleliste -> {
                    loadFragment(NouvelleListeFragment())  // Remplacez par le fragment NouvelleListe
                    true
                }
                R.id.nav_compte -> {
                    loadFragment(MonCompteFragment())  // Remplacez par le fragment MonCompte
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
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
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
}
