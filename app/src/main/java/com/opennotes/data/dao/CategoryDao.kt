package com.opennotes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.opennotes.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: com.opennotes.ui.Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>
}