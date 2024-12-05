package com.example.frigozen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frigozen.R
import com.example.frigozen.AlimentAdapter
import com.example.frigozen.Aliment
import com.example.frigozen.ListItem
import androidx.recyclerview.widget.DividerItemDecoration



class AlimentsFragment : Fragment(R.layout.aliments_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var selectedCategory: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCategory = arguments?.getString("selectedCategory") ?: ""
        val alimentsList = getAlimentsByCategory(selectedCategory)

        recyclerView = view.findViewById(R.id.recyclerViewAliments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = AlimentAdapter(alimentsList) { aliment ->
            Toast.makeText(requireContext(), "${aliment.name} ajouté à la liste!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAlimentsByCategory(category: String): MutableList<Aliment> {
        return when (category) {
            "Fruits" -> mutableListOf(
                Aliment("Pomme", R.drawable.bilan_nutritif_icon, "Fruits",52),
                Aliment("Banane", R.drawable.bilan_nutritif_icon, "Fruits",89)
            )
            "Légumes" -> mutableListOf(
                Aliment("Carotte", R.drawable.bilan_nutritif_icon, "Légumes",41),
                Aliment("Tomate", R.drawable.bilan_nutritif_icon, "Légumes",18)
            )
            else -> mutableListOf()
        }
    }

}

