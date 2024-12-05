package com.example.frigozen

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        db?.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Mettre à jour la base de données si nécessaire
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN imc REAL")
            db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN calories INTEGER")
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
                imc = cursor.getFloat(cursor.getColumnIndexOrThrow("imc")),
                calories = cursor.getFloat(cursor.getColumnIndexOrThrow("calories"))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun updateHealthData(email: String, imc: Float, calories: Float): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("imc", imc)
            put("calories", calories)
        }
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_EMAIL = ?",
            arrayOf(email)
        )
    }

}

// Classe pour représenter un utilisateur
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val imc: Float,
    val calories: Float
)
