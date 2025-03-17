package com.opennotes.data

import android.content.Context
import androidx.room.Room

object DatabaseHelper {
    private const val DATABASE_NAME = "open_notes_database"

    // Singleton instance of the database
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        // Return the existing database instance, or create a new one if null
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()

            INSTANCE = instance
            instance
        }
    }
}