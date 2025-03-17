package com.opennotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.opennotes.ui.NoteApp
import com.opennotes.ui.NotesViewModel
import com.opennotes.ui.theme.OpenNotesTheme


class MainActivity : ComponentActivity() {
    private lateinit var notesViewModel: NotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProvider(this)[NotesViewModel::class.java]

        setContent {
            OpenNotesTheme {
                NoteApp(notesViewModel)
            }
        }
    }
}