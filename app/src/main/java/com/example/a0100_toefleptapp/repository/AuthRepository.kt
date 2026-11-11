package com.example.a0100_toefleptapp.repository

import com.example.a0100_toefleptapp.model.*
import com.example.a0100_toefleptapp.service.ApiService
import com.example.a0100_toefleptapp.util.TokenManager

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(emailOrNim: String, pass: String): Result<AuthResponseData> {
        return try {
            val response = apiService.login(LoginRequest(emailOrNim, pass))
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                tokenManager.saveUser(data.user)
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login gagal. Periksa email/kata sandi."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        nama: String,
        nim: String,
        email: String,
        pass: String,
        role: String
    ): Result<AuthResponseData> {
        return try {
            val response = apiService.register(RegisterRequest(nama, nim, email, pass, role))
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                tokenManager.saveUser(data.user)
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registrasi gagal."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clear()
    }

    fun getCurrentUser(): User? = tokenManager.getUser()

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
}

