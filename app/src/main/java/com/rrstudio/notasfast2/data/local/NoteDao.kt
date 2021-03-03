package com.rrstudio.notasfast2.data.local

import androidx.room.*
import com.rrstudio.notasfast2.data.model.NoteEntity


@Dao
interface NoteDao {

    @Query("SELECT * FROM NoteEntity")
    fun getAllNotes(): MutableList<NoteEntity>

    @Query("SELECT * FROM NoteEntity where id = :id")
    fun getNoteById(id:Long) : NoteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: NoteEntity)

    @Insert
    fun addNote(noteEntity: NoteEntity): Long

    @Update
    fun updateNote(noteEntity: NoteEntity)

    @Delete
    fun deleteNote(noteEntity: NoteEntity)

}