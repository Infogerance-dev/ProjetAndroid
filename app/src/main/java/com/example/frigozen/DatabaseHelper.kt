package com.example.frigozen

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getFloatOrNull
import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "FrigoZen.db"
        const val DATABASE_VERSION = 2

        // Table pour les utilisateurs
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // Table listes de courses
        const val TABLE_SHOPPING_LISTS = "shopping_lists"
        const val COLUMN_LIST_ID = "id"
        const val COLUMN_LIST_NAME = "list_name"
        const val COLUMN_LIST_USER_ID = "user_id"

        // Table items
        const val TABLE_LIST_ITEMS = "list_items"
        const val COLUMN_ITEM_ID = "id"
        const val COLUMN_ITEM_NAME = "item_name"
        const val COLUMN_ITEM_QUANTITY = "quantity"
        const val COLUMN_ITEM_CALORIES = "calories"
        const val COLUMN_ITEM_LIST_ID = "shopping_list_id"

        // Table IMC
        const val TABLE_IMC = "lists_imc"
        const val COLUMN_IMC_ID = "id"
        const val COLUMN_IMC = "imc"
        const val COLUMN_CALPERDAY = "caloriesPerDay"
        const val COLUMN_LIST_USER_ID2 = "user_id"
        }


    override fun onCreate(db: SQLiteDatabase?) {
        // Création de la table utilisateur
        val createTableUsers = """
            CREATE TABLE $TABLE_USERS (

                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // Création de la table listes de courses
        val createTableShoppingLists = """
            CREATE TABLE $TABLE_SHOPPING_LISTS (
                $COLUMN_LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LIST_NAME TEXT NOT NULL,
                $COLUMN_LIST_USER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_LIST_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        // Création de la table Aliments
        val createTableListItems = """
            CREATE TABLE $TABLE_LIST_ITEMS (
                $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ITEM_NAME TEXT NOT NULL,
                $COLUMN_ITEM_LIST_ID INTEGER NOT NULL,
                $COLUMN_ITEM_QUANTITY INTEGER NOT NULL,   -- Nouvelle colonne pour la quantité
                $COLUMN_ITEM_CALORIES INTEGER NOT NULL,   -- Nouvelle colonne pour les calories
                FOREIGN KEY ($COLUMN_ITEM_LIST_ID) REFERENCES $TABLE_SHOPPING_LISTS($COLUMN_LIST_ID)
            )
        """.trimIndent()

        // Création de la table IMC
        val createTableIMC = """
            CREATE TABLE $TABLE_IMC (
                $COLUMN_IMC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMC REAL,
                $COLUMN_CALPERDAY INTEGER NOT NULL,   -- Nouvelle colonne pour les calories
                FOREIGN KEY ($COLUMN_LIST_USER_ID2) REFERENCES $TABLE_USERS($COLUMN_ID)            )
        """.trimIndent()

        db?.execSQL(createTableUsers)
        db?.execSQL(createTableShoppingLists)
        db?.execSQL(createTableListItems)
        db?.execSQL(createTableIMC)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Mettre à jour la base de données si nécessaire
        if (oldVersion < 2) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_SHOPPING_LISTS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIST_ITEMS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_IMC")
            onCreate(db)
        }
    }

    // Fonction pour ajouter un utilisateur
    fun insertUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }
    // Fonction pour vérifier si l'email n'est pas déjà existant dans la B
    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Fonction pour récupérer un utilisateur
    fun getUser(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                imc = cursor.getFloatOrNull(cursor.getColumnIndexOrThrow(COLUMN_IMC)),
                calories = cursor.getFloatOrNull(cursor.getColumnIndexOrThrow(COLUMN_CALPERDAY))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }


    // Méthode pour insérer une liste de courses
    fun insertShoppingList(userId: Int, listName: String, items: List<ListAliment>): Long {
        val db = writableDatabase
        db.beginTransaction() // Commencer une transaction pour garantir la cohérence des données
        var listId: Long = -1

        try {
            // Insertion dans la table `shopping_lists`
            val listValues = ContentValues().apply {
                put(COLUMN_LIST_NAME, listName)
                put(COLUMN_LIST_USER_ID, userId)
            }
            listId = db.insert(TABLE_SHOPPING_LISTS, null, listValues)

            // Vérifier si l'insertion a réussi
            if (listId == -1L) {
                throw Exception("Erreur lors de l'insertion de la liste.")
            }

            // Insertion des aliments dans la table `list_items`
            items.forEach { item ->
                val itemValues = ContentValues().apply {
                    put(COLUMN_ITEM_NAME, item.name)
                    put(COLUMN_ITEM_LIST_ID, listId)
                    put(COLUMN_ITEM_QUANTITY, item.quantity)  // Ajout de la quantité
                    put(COLUMN_ITEM_CALORIES, item.calories)  // Ajout des calories
                }

                val itemId = db.insert(TABLE_LIST_ITEMS, null, itemValues)

                if (itemId == -1L) {
                    throw Exception("Erreur lors de l'insertion de l'aliment : ${item.name}.")
                }
            }

            db.setTransactionSuccessful() // Marquer la transaction comme réussie
        } catch (e: Exception) {
            Log.e("DatabaseError", "Erreur lors de l'insertion de la liste ou des items : ${e.message}")
            e.printStackTrace()
        } finally {
            db.endTransaction() // Terminer la transaction
        }

        return listId
    }

    fun getShoppingListsByUser(userId: Int): List<ShoppingList> {
        val db = readableDatabase
        val shoppingLists = mutableListOf<ShoppingList>()

        // Requête pour récupérer les listes dans l'ordre décroissant par ID
        val cursor = db.query(
            TABLE_SHOPPING_LISTS,
            null,
            "$COLUMN_LIST_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_LIST_ID DESC" // Ajout du tri décroissant
        )

        if (cursor.moveToFirst()) {
            do {
                val listId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_ID))
                val listName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME))

                // Récupérer les items associés à cette liste
                val items = getItemsByListId(listId)
                shoppingLists.add(ShoppingList(listId, listName, userId, items))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return shoppingLists
    }


    fun getItemsByListId(listId: Int): List<ListAliment> {
        val db = readableDatabase
        val items = mutableListOf<ListAliment>()

        val cursor = db.query(
            TABLE_LIST_ITEMS,
            arrayOf(COLUMN_ITEM_NAME, COLUMN_ITEM_QUANTITY, COLUMN_ITEM_CALORIES),
            "$COLUMN_ITEM_LIST_ID = ?",
            arrayOf(listId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val itemName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
                val calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CALORIES))
                items.add(ListAliment(itemName, quantity, calories))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return items
    }

    fun updateHealthData(imc: Float, caloriesPerDay: Float) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_IMC, imc)
        values.put(COLUMN_CALPERDAY, caloriesPerDay)

        val rowsUpdated = db.update(TABLE_IMC, values, "$COLUMN_IMC = ?", arrayOf(imc.toString()) )
        if (rowsUpdated == 0) {
            values.put(COLUMN_IMC, imc)
            db.insert(TABLE_IMC, null, values)
        }
    }

    private fun Cursor.getFloatOrNull(columnName: String): Float? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getFloat(index) else null
    }

}


// Classe pour représenter un utilisateur
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val imc : Float?,
    val calories : Float?
)

@Parcelize
data class ShoppingList(
    val id: Int,
    val name: String,
    val userId: Int,
    val items: List<ListAliment>
) : Parcelable
