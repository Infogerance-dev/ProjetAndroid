import android.os.Bundle
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
                    loadFragment(NouvelleListeFragment())  // Remplacez par le fragment Search
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

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}
