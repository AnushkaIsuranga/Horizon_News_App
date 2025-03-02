package com.kahdse.horizonnewsapp

import com.kahdse.horizonnewsapp.model.Draft

class DraftRepository(private val draftDao: DraftDao) {
    suspend fun saveDraft(draft: Draft) = draftDao.saveDraft(draft)
    suspend fun getAllDrafts() = draftDao.getAllDrafts()
    suspend fun deleteDraft(draft: Draft) = draftDao.deleteDraft(draft)
}
