package com.example.a0100_toefleptapp.model

data class User(
    val id: Int,
    val nama_lengkap: String,
    val nim: String,
    val email: String,
    val role: String // "mahasiswa" atau "dosen"
)

data class AuthResponseData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class LoginRequest(
    val emailOrNim: String,
    val password: String
)

data class RegisterRequest(
    val nama_lengkap: String,
    val nim: String,
    val email: String,
    val password: String,
    val role: String = "mahasiswa"
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponseData(
    val accessToken: String,
    val refreshToken: String
)

