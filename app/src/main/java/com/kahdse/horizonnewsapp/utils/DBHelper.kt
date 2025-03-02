package com.kahdse.horizonnewsapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_DRAFTS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITLE TEXT, " +
                    "$COLUMN_CONTENT TEXT, " +
                    "$COLUMN_IMAGE_URI TEXT, " +
                    "$COLUMN_CREATED_DATE INTEGER, " +
                    "$COLUMN_LAST_ACCESSED INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DRAFTS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "drafts.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_DRAFTS = "drafts"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_IMAGE_URI = "imageUri"
        const val COLUMN_CREATED_DATE = "createdDate"
        const val COLUMN_LAST_ACCESSED = "lastAccessed"
    }
}
