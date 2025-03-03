package com.opennotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val categoryId: Int,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

