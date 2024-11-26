import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.frigozen.R
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Définir un listener pour gérer les clics
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_meslistes -> MesListesFragment()
                R.id.nav_nouvelleliste -> NouvelleListeFragment()
                R.id.nav_bilannutritif -> BilanNutritifFragment()
                R.id.nav_compte -> MonCompteFragment()
                else -> BilanNutritifFragment()
            }

            // Remplacer le fragment affiché
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, selectedFragment)
                .commit()

            true
        }

        // Définir le fragment par défaut
        bottomNavigationView.selectedItemId = R.id.nav_bilannutritif
    }
}
