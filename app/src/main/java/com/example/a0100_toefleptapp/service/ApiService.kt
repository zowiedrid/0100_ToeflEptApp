package com.example.a0100_toefleptapp.service

import com.example.a0100_toefleptapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 1. AUTH
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<RefreshTokenResponseData>>

    @GET("api/auth/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>

    // 2. PACKAGES (CRUDS)
    @GET("api/packages")
    suspend fun getPackages(@Query("search") search: String? = null): Response<ApiResponse<List<TestPackage>>>

    @GET("api/packages/{id}")
    suspend fun getPackageById(@Path("id") id: String): Response<ApiResponse<TestPackage>>

    @POST("api/packages")
    suspend fun createPackage(@Body request: CreatePackageRequest): Response<ApiResponse<TestPackage>>

    @PUT("api/packages/{id}")
    suspend fun updatePackage(@Path("id") id: String, @Body request: CreatePackageRequest): Response<ApiResponse<TestPackage>>

    @DELETE("api/packages/{id}")
    suspend fun deletePackage(@Path("id") id: String): Response<ApiResponse<Unit>>

    // 3. QUESTIONS (CRUDS)
    @GET("api/questions")
    suspend fun getQuestions(
        @Query("package_id") packageId: String? = null,
        @Query("part") part: Int? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<Question>>>

    @GET("api/questions/{id}")
    suspend fun getQuestionById(@Path("id") id: String): Response<ApiResponse<Question>>

    @POST("api/questions")
    suspend fun createQuestion(@Body request: CreateQuestionRequest): Response<ApiResponse<Question>>

    @PUT("api/questions/{id}")
    suspend fun updateQuestion(@Path("id") id: String, @Body request: CreateQuestionRequest): Response<ApiResponse<Question>>

    @DELETE("api/questions/{id}")
    suspend fun deleteQuestion(@Path("id") id: String): Response<ApiResponse<Unit>>

    // 4. ATTEMPTS (CRUDS)
    @POST("api/attempts")
    suspend fun submitAttempt(@Body request: SubmitAttemptRequest): Response<ApiResponse<TestAttempt>>

    @GET("api/attempts")
    suspend fun getUserAttempts(@Query("search") search: String? = null): Response<ApiResponse<List<TestAttempt>>>

    @GET("api/attempts/{id}")
    suspend fun getAttemptById(@Path("id") id: String): Response<ApiResponse<TestAttempt>>

    @DELETE("api/attempts/{id}")
    suspend fun deleteAttempt(@Path("id") id: String): Response<ApiResponse<Unit>>

    // 5. BOOKMARKS (CRUDS)
    @GET("api/bookmarks")
    suspend fun getBookmarks(@Query("search") search: String? = null): Response<ApiResponse<List<Bookmark>>>

    @POST("api/bookmarks")
    suspend fun createBookmark(@Body request: CreateBookmarkRequest): Response<ApiResponse<Bookmark>>

    @PUT("api/bookmarks/{id}")
    suspend fun updateBookmark(@Path("id") id: Int, @Body request: UpdateBookmarkRequest): Response<ApiResponse<Bookmark>>

    @DELETE("api/bookmarks/{id}")
    suspend fun deleteBookmark(@Path("id") id: Int): Response<ApiResponse<Unit>>
}

