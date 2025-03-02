package com.kahdse.horizonnewsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drafts")
data class Draft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val imageUri: String?,
    val createdDate: Long,
    val lastAccessed: Long
)
