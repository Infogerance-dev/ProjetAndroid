package com.example.frigozen


data class Aliment(
    val name: String,
    val imageResId: Int,
    val category: String,
    val calories: Int, // pour 100g
    val quantity: Int = 0
) : ListItem()



