package com.example.mynotes.data.repository

import com.example.mynotes.domain.db.NoteDatabase
import com.example.mynotes.domain.entities.Note

class NoteRepository(private val db: NoteDatabase) {

    fun getAllNotes() = db.getNoteDao().getAllNotes()

    fun searchNote(query: String) = db.getNoteDao().searchNote(query)

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)
}