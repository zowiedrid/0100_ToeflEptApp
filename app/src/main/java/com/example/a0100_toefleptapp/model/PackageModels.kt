package com.example.a0100_toefleptapp.model

data class TestPackage(
    val id: String,
    val title: String,
    val short_description: String,
    val badge: String? = null,
    val target_focus: String? = null,
    val duration_minutes: Int = 115,
    val totalQuestions: Int = 0,
    val questions: List<Question>? = null
)

data class Question(
    val id: String,
    val package_id: String,
    val number: Int,
    val part: Int, // 1 = Listening, 2 = Structure, 3 = Reading
    val type: String,
    val prompt: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_answer: String, // "a", "b", "c", "d"
    val explanation: String? = null,
    val audio_script: String? = null,
    val audio_speaker: String? = null,
    val passage_text: String? = null
)

data class CreatePackageRequest(
    val id: String,
    val title: String,
    val short_description: String,
    val badge: String = "Soal Latihan EPT",
    val target_focus: String? = null,
    val duration_minutes: Int = 115
)

data class CreateQuestionRequest(
    val id: String,
    val package_id: String,
    val number: Int,
    val part: Int,
    val type: String,
    val prompt: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_answer: String,
    val explanation: String? = null
)

