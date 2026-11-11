package com.example.a0100_toefleptapp.model

data class TestAttempt(
    val id: String,
    val user_id: Int,
    val package_id: String,
    val package_title: String,
    val listening_raw: Int,
    val listening_total: Int,
    val listening_scaled: Int,
    val structure_raw: Int,
    val structure_total: Int,
    val structure_scaled: Int,
    val reading_raw: Int,
    val reading_total: Int,
    val reading_scaled: Int,
    val total_score: Int,
    val is_passed: Boolean,
    val score_gap: Int,
    val time_spent_seconds: Int,
    val user_answers_json: String? = null,
    val attempt_date: String? = null,
    val package_data: TestPackage? = null
)

data class SubmitAttemptRequest(
    val package_id: String,
    val user_answers: Map<String, String>, // questionId -> "a"/"b"/"c"/"d"
    val time_spent_seconds: Int
)

