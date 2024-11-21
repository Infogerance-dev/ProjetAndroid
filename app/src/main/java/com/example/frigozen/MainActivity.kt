package com.example.frigozen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodList: MutableList<Food>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation de la liste et de l'adaptateur
        foodList = mutableListOf()
        foodAdapter = FoodAdapter(foodList)

        // RecyclerView
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recyclerViewFood)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = foodAdapter



        // Bouton "Ajouter" pour ouvrir le dialogue
        val btnAddFood: Button = findViewById(R.id.btnAddFood)
        btnAddFood.setOnClickListener {
            showAddFoodDialog()
        }
    }

    private fun showAddFoodDialog() {
        // Création du dialogue pour ajouter un aliment
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_food, null)
        val foodNameEditText: EditText = dialogView.findViewById(R.id.editFoodName)
        val foodCaloriesEditText: EditText = dialogView.findViewById(R.id.editFoodCalories)

        // Affichage du dialogue
        MaterialAlertDialogBuilder(this)
            .setTitle("Ajouter un aliment")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { dialog, which ->
                val name = foodNameEditText.text.toString()
                val calories = foodCaloriesEditText.text.toString()

                if (name.isNotEmpty() && calories.isNotEmpty()) {
                    val food = Food(name, calories.toInt())
                    foodList.add(food)
                    foodAdapter.notifyItemInserted(foodList.size - 1)
                    Toast.makeText(this, "Aliment ajouté", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // Classe FoodAdapter pour gérer l'affichage des aliments dans le RecyclerView
    class FoodAdapter(private val foodList: MutableList<Food>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
            // Inflate le layout de chaque item
            val inflater = LayoutInflater.from(parent.context)
            val binding = inflater.inflate(R.layout.list_item_food, parent, false)
            return FoodViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
            // Remplir les vues avec les données de l'aliment
            val food = foodList[position]
            holder.bind(food)
        }

        override fun getItemCount(): Int {
            return foodList.size
        }

        // ViewHolder pour chaque item dans la liste
        class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
            private val foodName: TextView = itemView.findViewById(R.id.foodName)
            private val foodCalories: TextView = itemView.findViewById(R.id.foodCalories)

            fun bind(food: Food) {
                // Associer les données de l'aliment aux vues
                foodName.text = food.name
                foodCalories.text = "${food.calories} kcal"
                // Exemple d'image (vous pouvez personnaliser en fonction des besoins)
                foodImage.setImageResource(R.drawable.ic_launcher_foreground)  // Assurez-vous d'avoir l'image dans res/drawable
            }
        }
    }

    // Classe Food représentant les aliments avec un nom et une quantité de calories
    data class Food(
        val name: String,
        val calories: Int
    )
}
