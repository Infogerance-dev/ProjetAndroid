package com.example.frigozen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class ListItem {
    data class Category(val name: String) : ListItem() // Pour représenter une catégorie
    data class AlimentItem(val aliment: Aliment) : ListItem() // Pour représenter un aliment individuel
}

@Parcelize
data class ListAliment(
    val name: String,
    val quantity: Int,
    val calories: Int
) : Parcelable
