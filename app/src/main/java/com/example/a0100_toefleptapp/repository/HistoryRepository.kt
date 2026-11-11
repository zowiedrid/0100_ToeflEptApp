package com.example.a0100_toefleptapp.repository

import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.service.ApiService

class HistoryRepository(private val apiService: ApiService) {

    suspend fun getAttempts(search: String? = null): Result<List<TestAttempt>> {
        return try {
            val response = apiService.getUserAttempts(search)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat riwayat nilai."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAttemptDetail(id: String): Result<TestAttempt> {
        return try {
            val response = apiService.getAttemptById(id)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat detail hasil."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAttempt(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteAttempt(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus riwayat."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
