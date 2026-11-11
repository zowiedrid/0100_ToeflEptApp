package com.example.a0100_toefleptapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(
        val attempts: List<TestAttempt>,
        val totalAttempts: Int,
        val highestScore: Int,
        val averageScore: Int,
        val passCount: Int
    ) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadAttempts()
    }

    fun loadAttempts(query: String? = null) {
        _uiState.value = HistoryUiState.Loading
        viewModelScope.launch {
            val result = repository.getAttempts(query)
            result.onSuccess { list ->
                val total = list.size
                val highest = if (total > 0) list.maxOf { it.total_score } else 0
                val avg = if (total > 0) list.sumOf { it.total_score } / total else 0
                val pass = list.count { it.is_passed }

                _uiState.value = HistoryUiState.Success(
                    attempts = list,
                    totalAttempts = total,
                    highestScore = highest,
                    averageScore = avg,
                    passCount = pass
                )
            }.onFailure { err ->
                _uiState.value = HistoryUiState.Error(err.message ?: "Gagal memuat riwayat.")
            }
        }
    }

    fun onSearchChanged(newQuery: String) {
        _searchQuery.value = newQuery
        loadAttempts(if (newQuery.isBlank()) null else newQuery)
    }

    fun deleteAttempt(id: String) {
        viewModelScope.launch {
            val result = repository.deleteAttempt(id)
            result.onSuccess {
                loadAttempts(_searchQuery.value)
            }
        }
    }
}

