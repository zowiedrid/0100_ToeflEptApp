package com.example.a0100_toefleptapp.repository

import com.example.a0100_toefleptapp.model.*
import com.example.a0100_toefleptapp.service.ApiService

class BookmarkRepository(private val apiService: ApiService) {

    suspend fun getBookmarks(search: String? = null): Result<List<Bookmark>> {
        return try {
            val response = apiService.getBookmarks(search)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat bookmark."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBookmark(questionId: String, notes: String? = null): Result<Bookmark> {
        return try {
            val response = apiService.createBookmark(CreateBookmarkRequest(questionId, notes))
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menyimpan bookmark."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNote(bookmarkId: Int, notes: String): Result<Bookmark> {
        return try {
            val response = apiService.updateBookmark(bookmarkId, UpdateBookmarkRequest(notes))
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memperbarui catatan."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBookmark(bookmarkId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteBookmark(bookmarkId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus bookmark."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
