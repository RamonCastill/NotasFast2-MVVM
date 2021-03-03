package com.rrstudio.notasfast2.data.local

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class NoteAplication: Application() {

    companion object{
        lateinit var database: NoteDatabase

    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE NoteEntity ADD COLUMN imageBitmap BYTEARRAY")
            }
        }

        database = Room.databaseBuilder(this,
            NoteDatabase::class.java,
            "NoteDatabase")
                .addMigrations(MIGRATION_1_2)
                .build()
    }

}