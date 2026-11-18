package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ: Flow dari Room diubah menjadi StateFlow untuk UI
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
    fun addNote(title: String, note: String) {
        val t = title.trim()
        if (t.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(title = t, note = note.trim())) }
    }

    // UPDATE
    fun updateNote(item: Note, newTitle: String, newNote: String) {
        val t = newTitle.trim()
        if (t.isEmpty()) return
        viewModelScope.launch { dao.update(item.copy(title = t, note = newNote.trim())) }
    }

    // DELETE
    fun deleteNote(item: Note) {
        viewModelScope.launch { dao.delete(item) }
    }
}