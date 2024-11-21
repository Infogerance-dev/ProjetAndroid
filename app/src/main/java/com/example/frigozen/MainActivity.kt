import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frigozen.R

class MainActivity : AppCompatActivity() {

    // Une liste mutable pour stocker les aliments
    private val foodList = mutableListOf<FoodItem>()
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuration du RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFood)
        foodAdapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = foodAdapter

        // Bouton "Ajouter"
        val btnAddFood = findViewById<android.widget.Button>(R.id.btnAddFood)
        btnAddFood.setOnClickListener {
            showAddFoodDialog()
        }
    }

    // Fonction pour afficher la boîte de dialogue
    private fun showAddFoodDialog() {
        // Charger la vue du dialogue
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_food, null)

        // Création du builder pour le dialogue
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ajouter un aliment")

        // Récupérer les champs de saisie dans le layout
        val editFoodName = dialogView.findViewById<EditText>(R.id.editFoodName)
        val editFoodCalories = dialogView.findViewById<EditText>(R.id.editFoodCalories)

        // Ajouter des actions pour les boutons
        dialogBuilder.setPositiveButton("Ajouter") { dialog, _ ->
            val foodName = editFoodName.text.toString()
            val foodCalories = editFoodCalories.text.toString().toIntOrNull()

            // Vérification des champs
            if (foodName.isNotBlank() && foodCalories != null) {
                // Ajouter l'aliment à la liste
                foodList.add(FoodItem(foodName, foodCalories))
                foodAdapter.notifyDataSetChanged()
                Toast.makeText(this, "$foodName ajouté avec succès !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Veuillez remplir correctement les champs.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.dismiss()
        }

        // Afficher le dialogue
        val dialog = dialogBuilder.create()
        dialog.show()
    }
}

// Classe de données pour représenter un aliment
data class FoodItem(val name: String, val calories: Int)

// Adaptateur pour le RecyclerView
class FoodAdapter(private val items: List<FoodItem>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listealiments, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    class FoodViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: android.widget.TextView = itemView.findViewById(R.id.textFoodName)
        private val foodCalories: android.widget.TextView = itemView.findViewById(R.id.textFoodCalories)

        fun bind(foodItem: FoodItem) {
            foodName.text = foodItem.name
            foodCalories.text = "${foodItem.calories} kcal"
        }
    }
}
