package com.example.frigozen

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getFloatOrNull

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "frigozen.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_USERS = "users"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_IMC = "imc"
        private const val COLUMN_CALORIES = "calories"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_EMAIL TEXT PRIMARY KEY,
                $COLUMN_IMC REAL,
                $COLUMN_CALORIES REAL
            )
        """.trimIndent()
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_IMC REAL")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_CALORIES REAL")
        }
    }

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

        return if (cursor != null && cursor.moveToFirst()) {
            val imc = cursor.getFloatOrNull(cursor.getColumnIndexOrThrow(COLUMN_IMC))
            val calories = cursor.getFloatOrNull(cursor.getColumnIndexOrThrow(COLUMN_CALORIES))
            cursor.close()
            User(email, imc, calories)
        } else {
            cursor?.close()
            null
        }
    }

    fun updateHealthData(email: String, imc: Float, calories: Float) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_IMC, imc)
        values.put(COLUMN_CALORIES, calories)

        val rowsUpdated = db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        if (rowsUpdated == 0) {
            values.put(COLUMN_EMAIL, email)
            db.insert(TABLE_USERS, null, values)
        }
    }

    private fun Cursor.getFloatOrNull(columnName: String): Float? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getFloat(index) else null
    }
}
data class User(
    val email: String,
    val imc: Float?,
    val calories: Float?
)
