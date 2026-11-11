package com.example.a0100_toefleptapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.Question
import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.model.TestPackage
import com.example.a0100_toefleptapp.repository.TestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TestUiState {
    object Loading : TestUiState()
    data class InProgress(
        val pkg: TestPackage,
        val currentQuestionIndex: Int,
        val currentPart: Int,
        val userAnswers: Map<String, String>, // questionId -> option "a" | "b" | "c" | "d"
        val timeRemainingSeconds: Int
    ) : TestUiState()
    data class Submitted(val attempt: TestAttempt) : TestUiState()
    data class Error(val message: String) : TestUiState()
}

class TestViewModel(private val repository: TestRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<TestUiState>(TestUiState.Loading)
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    private var currentPackage: TestPackage? = null
    private var currentIndex = 0
    private var currentPart = 1
    private val userAnswers = mutableMapOf<String, String>()
    private var timeSpent = 0
    private var timeRemaining = 35 * 60 // 35 minutes for Part 1
    private var timerJob: Job? = null

    fun loadTestPackage(packageId: String) {
        _uiState.value = TestUiState.Loading
        viewModelScope.launch {
            val result = repository.getPackageDetail(packageId)
            result.onSuccess { pkg ->
                currentPackage = pkg
                currentIndex = 0
                currentPart = 1
                userAnswers.clear()
                timeSpent = 0
                timeRemaining = 35 * 60
                startTimer()
                updateInProgressState()
            }.onFailure { err ->
                _uiState.value = TestUiState.Error(err.message ?: "Gagal memuat soal tes.")
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
                timeSpent++
                updateInProgressState()
            }
        }
    }

    private fun updateInProgressState() {
        val pkg = currentPackage ?: return
        _uiState.value = TestUiState.InProgress(
            pkg = pkg,
            currentQuestionIndex = currentIndex,
            currentPart = currentPart,
            userAnswers = userAnswers.toMap(),
            timeRemainingSeconds = timeRemaining
        )
    }

    fun selectAnswer(questionId: String, answer: String) {
        userAnswers[questionId] = answer
        updateInProgressState()
    }

    fun goToNextQuestion() {
        val total = currentPackage?.questions?.size ?: 0
        if (currentIndex < total - 1) {
            currentIndex++
            // update active part based on question
            currentPackage?.questions?.getOrNull(currentIndex)?.let { q ->
                if (q.part != currentPart) {
                    currentPart = q.part
                    // adjust timer based on part
                    timeRemaining = when (currentPart) {
                        2 -> 25 * 60
                        3 -> 55 * 60
                        else -> 35 * 60
                    }
                }
            }
            updateInProgressState()
        }
    }

    fun goToPreviousQuestion() {
        if (currentIndex > 0) {
            currentIndex--
            currentPackage?.questions?.getOrNull(currentIndex)?.let { q ->
                currentPart = q.part
            }
            updateInProgressState()
        }
    }

    fun jumpToQuestion(index: Int) {
        val total = currentPackage?.questions?.size ?: 0
        if (index in 0 until total) {
            currentIndex = index
            currentPackage?.questions?.getOrNull(currentIndex)?.let { q ->
                currentPart = q.part
            }
            updateInProgressState()
        }
    }

    fun switchPart(part: Int) {
        val firstQuestionOfPart = currentPackage?.questions?.indexOfFirst { it.part == part }
        if (firstQuestionOfPart != null && firstQuestionOfPart >= 0) {
            currentIndex = firstQuestionOfPart
            currentPart = part
            timeRemaining = when (part) {
                2 -> 25 * 60
                3 -> 55 * 60
                else -> 35 * 60
            }
            updateInProgressState()
        }
    }

    fun submitTest() {
        timerJob?.cancel()
        val pkg = currentPackage ?: return
        _uiState.value = TestUiState.Loading

        viewModelScope.launch {
            val result = repository.submitAttempt(pkg.id, userAnswers, timeSpent)
            result.onSuccess { attempt ->
                _uiState.value = TestUiState.Submitted(attempt)
            }.onFailure { err ->
                _uiState.value = TestUiState.Error(err.message ?: "Gagal mengirim jawaban.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

