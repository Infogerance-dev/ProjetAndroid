package com.example.frigozen

sealed class ListItem {
    data class AlimentItem(val aliment: Aliment) : ListItem()
    data class Category(val name: String) : ListItem()
}


