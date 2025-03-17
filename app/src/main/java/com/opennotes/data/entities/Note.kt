package com.opennotes.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: String,
    val content: String,
    val categoryId: String,
    @ColumnInfo(name = "created")
    val creationTime: Long = System.currentTimeMillis()
)

