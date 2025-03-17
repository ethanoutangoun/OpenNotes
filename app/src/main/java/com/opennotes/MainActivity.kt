package com.opennotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.opennotes.ui.NoteApp
import com.opennotes.ui.theme.OpenNotesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenNotesTheme {
                NoteApp()
            }
        }
    }
}