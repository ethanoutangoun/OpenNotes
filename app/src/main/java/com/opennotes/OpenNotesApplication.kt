package com.opennotes

import android.app.Application
import com.opennotes.data.DatabaseHelper
import com.opennotes.data.AppDatabase

class OpenNotesApplication : Application() {
    // Lazy initialization of the database

    override fun onCreate() {
        super.onCreate()
    }
}