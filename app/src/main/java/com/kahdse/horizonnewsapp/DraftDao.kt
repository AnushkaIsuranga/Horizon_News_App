package com.kahdse.horizonnewsapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kahdse.horizonnewsapp.model.Draft

@Dao
interface DraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: Draft)

    @Query("SELECT * FROM drafts ORDER BY lastAccessed DESC")
    suspend fun getAllDrafts(): List<Draft>

    @Delete
    suspend fun deleteDraft(draft: Draft)
}
