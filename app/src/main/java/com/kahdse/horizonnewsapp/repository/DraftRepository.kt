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
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_TITLE, draft.title)
            put(DBHelper.COLUMN_CONTENT, draft.content)
            put(DBHelper.COLUMN_IMAGE_URI, draft.imageUri)
            put(DBHelper.COLUMN_CATEGORY, draft.category)
            put(DBHelper.COLUMN_CREATED_DATE, draft.createdDate)
            put(DBHelper.COLUMN_LAST_ACCESSED, draft.lastAccessed)
        }
        val result = db.insert(DBHelper.TABLE_DRAFTS, null, values)
        db.close()

        Log.d("DraftDebug", "Draft saved with ID: $result")
        return result
    }

    fun getAllDrafts(): List<Draft> {
        val drafts = mutableListOf<Draft>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_DRAFTS,
            null, null, null, null, null,
            "${DBHelper.COLUMN_LAST_ACCESSED} DESC"
        )
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
        cursor.close()
        return drafts
    }

    fun updateDraft(draft: Draft): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_TITLE, draft.title)
            put(DBHelper.COLUMN_CONTENT, draft.content)
            put(DBHelper.COLUMN_IMAGE_URI, draft.imageUri)
            put(DBHelper.COLUMN_CATEGORY, draft.category)
            put(DBHelper.COLUMN_LAST_ACCESSED, draft.lastAccessed)
        }

        val result = db.update(DBHelper.TABLE_DRAFTS, values, "$DBHelper.COLUMN_ID = ?", arrayOf(draft.id.toString()))
        db.close()
        return result > 0
    }

    fun getDraftById(draftId: Int): Draft? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_DRAFTS,
            null, "${DBHelper.COLUMN_ID} = ?", arrayOf(draftId.toString()), null, null, null
        )
        val categoryIndex = cursor.getColumnIndex(DBHelper.COLUMN_CATEGORY)

        return if (cursor.moveToFirst()) {
            val draft = Draft(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT)),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGE_URI)),
                category = if (categoryIndex != -1) cursor.getString(categoryIndex) else "Uncategorized",
                createdDate = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATED_DATE)),
                lastAccessed = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LAST_ACCESSED))
            )
            Log.d("DraftDebug", "Draft found: $draft")
            cursor.close()
            draft
        } else {
            Log.d("DraftDebug", "No draft found for ID: $draftId")
            cursor.close()
            null
        }
    }

    fun deleteDraft(draftId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DBHelper.TABLE_DRAFTS,
            "${DBHelper.COLUMN_ID}=?",
            arrayOf(draftId.toString())
        )
        db.close()
        return result
    }
}