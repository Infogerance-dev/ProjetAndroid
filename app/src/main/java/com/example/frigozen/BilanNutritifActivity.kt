import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.frigozen.R

class BilanNutritifActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilan_nutritif)

        // Récupération des vues
        val etPoids: EditText = findViewById(R.id.et_poids)
        val etTaille: EditText = findViewById(R.id.et_taille)
        val btnCalculer: Button = findViewById(R.id.btn_calculer)
        val tvIMC: TextView = findViewById(R.id.tv_imc)
        val tvCalories: TextView = findViewById(R.id.tv_calories)

        btnCalculer.setOnClickListener {
            val poids = etPoids.text.toString().toDoubleOrNull()
            val taille = etTaille.text.toString().toDoubleOrNull()

            if (poids == null || taille == null || poids <= 0 || taille <= 0) {
                Toast.makeText(this, "Veuillez entrer des valeurs valides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calcul de l'IMC
            val imc = poids / (taille * taille)
            tvIMC.text = "IMC : %.2f".format(imc)
            tvIMC.visibility = TextView.VISIBLE

            // Calcul des calories de base
            var caloriesBase = poids * 23

            // Afficher la pop-up pour l'activité physique
            val niveaux = arrayOf("Sédentaire", "Modérément actif", "Très actif")
            AlertDialog.Builder(this)
                .setTitle("Niveau d'activité physique")
                .setItems(niveaux) { _, which ->
                    val multiplicateur = when (which) {
                        0 -> 1.2
                        1 -> 1.5
                        2 -> 1.8
                        else -> 1.0
                    }

                    // Ajustement des calories selon le niveau d'activité
                    var calories = caloriesBase * multiplicateur

                    // Ajustement supplémentaire selon l'IMC
                    calories += when {
                        imc < 18.5 -> 400
                        imc > 25 -> -400
                        else -> 0
                    }

                    tvCalories.text = "Calories recommandées : %.0f kcal/jour".format(calories)
                    tvCalories.visibility = TextView.VISIBLE
                }
                .show()
        }
    }
}
