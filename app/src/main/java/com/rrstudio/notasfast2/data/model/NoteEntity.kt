package com.rrstudio.notasfast2.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "NoteEntity")
data class NoteEntity(@PrimaryKey(autoGenerate = true)
                      var id: Long = 0,
                      @ColumnInfo(name = "name")
                      var name: String,
                      @ColumnInfo(name = "description")
                      var description: String,
                      @ColumnInfo(name = "date")
                      var date: String = "",
                      @ColumnInfo(name = "image")
                      var image: String = ""){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}



