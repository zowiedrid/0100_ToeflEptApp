package com.example.a0100_toefleptapp.model

data class Bookmark(
    val id: Int,
    val user_id: Int,
    val question_id: String,
    val notes: String? = null,
    val question: Question? = null
)

data class CreateBookmarkRequest(
    val question_id: String,
    val notes: String? = null
)

data class UpdateBookmarkRequest(
    val notes: String
)

