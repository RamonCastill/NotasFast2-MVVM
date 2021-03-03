package com.rrstudio.notasfast2.repository

import com.rrstudio.notasfast2.data.model.NoteEntity

interface onClickListener {
    fun onClick(noteEntity: NoteEntity)
    fun onDeleteNote(noteEntity: NoteEntity)
    fun hideFab(isVisible: Boolean = false)
    fun addNote(noteEntity: NoteEntity)
    fun updateNote(noteEntity: NoteEntity)
}