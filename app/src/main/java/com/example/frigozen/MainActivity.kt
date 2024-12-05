package com.example.frigozen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Masquer/afficher le BottomNavigationView selon le fragment actif
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (currentFragment is LoginFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // Configurer la navigation pour les fragments principaux
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meslistes -> {
                    loadFragment(MesListesFragment())
                    true
                }
                R.id.nav_nouvelleliste -> {
                    loadFragment(NouvelleListeFragment())
                    true
                }
                R.id.nav_compte -> {
                    loadFragment(AccountCreationFragment())
                    true
                }
                R.id.nav_bilannutritif -> {
                    loadFragment(BilanNutritifFragment())
                    true
                }
                else -> false
            }
        }

        // Charger LoginFragment au démarrage
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .commit()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Navigation après une connexion réussie
    fun navigateToDefaultFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, MesListesFragment())
            .commit()
    }
}
