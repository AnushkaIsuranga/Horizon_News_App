package com.kahdse.horizonnewsapp.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.kahdse.horizonnewsapp.model.Draft
import com.kahdse.horizonnewsapp.utils.DBHelper

class DraftRepository(context: Context) {
    private val dbHelper = DBHelper(context)

    fun saveDraft(draft: Draft): Long {
        return try {
            dbHelper.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(DBHelper.COLUMN_TITLE, draft.title)
                    put(DBHelper.COLUMN_CONTENT, draft.content)
                    put(DBHelper.COLUMN_IMAGE_URI, draft.imageUri ?: "")
                    put(DBHelper.COLUMN_CATEGORY, draft.category ?: "Uncategorized")
                    put(DBHelper.COLUMN_CREATED_DATE, draft.createdDate)
                    put(DBHelper.COLUMN_LAST_ACCESSED, draft.lastAccessed)
                }
                val result = db.insert(DBHelper.TABLE_DRAFTS, null, values)
                Log.d("DraftDebug", "Draft saved with ID: $result")
                result
            }
        } catch (e: Exception) {
            Log.e("DraftDebug", "Error saving draft: ${e.message}")
            -1
        }
    }

    fun getAllDrafts(): List<Draft> {
        val drafts = mutableListOf<Draft>()
        try {
            dbHelper.readableDatabase.use { db ->
                db.query(
                    DBHelper.TABLE_DRAFTS,
                    null, null, null, null, null,
                    "${DBHelper.COLUMN_LAST_ACCESSED} DESC"
                ).use { cursor ->
                    val categoryIndex = cursor.getColumnIndex(DBHelper.COLUMN_CATEGORY)
                    while (cursor.moveToNext()) {
                        val draft = Draft(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                            title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)),
                            content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)),
                            imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_URI)),
                            category = if (categoryIndex != -1) cursor.getString(categoryIndex) else "Uncategorized",
                            createdDate = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATED_DATE)),
                            lastAccessed = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LAST_ACCESSED))
                        )
                        drafts.add(draft)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DraftDebug", "Error fetching drafts: ${e.message}")
        }
        return drafts
    }

    fun updateDraft(draft: Draft): Boolean {
        return try {
            dbHelper.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(DBHelper.COLUMN_TITLE, draft.title)
                    put(DBHelper.COLUMN_CONTENT, draft.content)
                    put(DBHelper.COLUMN_IMAGE_URI, draft.imageUri ?: "")
                    put(DBHelper.COLUMN_CATEGORY, draft.category ?: "Uncategorized")
                    put(DBHelper.COLUMN_LAST_ACCESSED, draft.lastAccessed)
                }
                val result = db.update(DBHelper.TABLE_DRAFTS, values, "${DBHelper.COLUMN_ID} = ?", arrayOf(draft.id.toString()))
                result > 0
            }
        } catch (e: Exception) {
            Log.e("DraftDebug", "Error updating draft: ${e.message}")
            false
        }
    }

    fun getDraftById(draftId: Int): Draft? {
        return try {
            dbHelper.readableDatabase.use { db ->
                db.query(
                    DBHelper.TABLE_DRAFTS,
                    null, "${DBHelper.COLUMN_ID} = ?", arrayOf(draftId.toString()), null, null, null
                ).use { cursor ->
                    if (cursor.moveToFirst()) {
                        val categoryIndex = cursor.getColumnIndex(DBHelper.COLUMN_CATEGORY)
                        Draft(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                            title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)),
                            content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)),
                            imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_URI)),
                            category = if (categoryIndex != -1) cursor.getString(categoryIndex) else "Uncategorized",
                            createdDate = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATED_DATE)),
                            lastAccessed = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LAST_ACCESSED))
                        )
                    } else {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DraftDebug", "Error fetching draft by ID: ${e.message}")
            null
        }
    }

    fun deleteDraft(draftId: Int): Int {
        return try {
            dbHelper.writableDatabase.use { db ->
                db.delete(
                    DBHelper.TABLE_DRAFTS,
                    "${DBHelper.COLUMN_ID} = ?",
                    arrayOf(draftId.toString())
                )
            }
        } catch (e: Exception) {
            Log.e("DraftDebug", "Error deleting draft: ${e.message}")
            -1
        }
    }
}