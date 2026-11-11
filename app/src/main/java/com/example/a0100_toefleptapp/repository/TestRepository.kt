package com.example.a0100_toefleptapp.repository

import com.example.a0100_toefleptapp.model.*
import com.example.a0100_toefleptapp.service.ApiService

class TestRepository(private val apiService: ApiService) {

    suspend fun getPackages(search: String? = null): Result<List<TestPackage>> {
        return try {
            val response = apiService.getPackages(search)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat paket soal."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPackageDetail(id: String): Result<TestPackage> {
        return try {
            val response = apiService.getPackageById(id)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat detail paket."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPackage(req: CreatePackageRequest): Result<TestPackage> {
        return try {
            val response = apiService.createPackage(req)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menambahkan paket."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitAttempt(packageId: String, userAnswers: Map<String, String>, timeSpentSeconds: Int): Result<TestAttempt> {
        return try {
            val req = SubmitAttemptRequest(packageId, userAnswers, timeSpentSeconds)
            val response = apiService.submitAttempt(req)
            val data = response.body()?.data
            if (response.isSuccessful && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengirim jawaban tes."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
