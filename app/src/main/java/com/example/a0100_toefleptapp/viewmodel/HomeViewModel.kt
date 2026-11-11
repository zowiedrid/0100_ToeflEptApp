package com.example.a0100_toefleptapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.CreatePackageRequest
import com.example.a0100_toefleptapp.model.TestPackage
import com.example.a0100_toefleptapp.repository.TestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val packages: List<TestPackage>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: TestRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadPackages()
    }

    fun loadPackages(query: String? = null) {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            val result = repository.getPackages(query)
            result.onSuccess { list ->
                _uiState.value = HomeUiState.Success(list)
            }.onFailure { err ->
                _uiState.value = HomeUiState.Error(err.message ?: "Gagal memuat paket.")
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        loadPackages(if (newQuery.isBlank()) null else newQuery)
    }

    fun createPackage(
        id: String,
        title: String,
        desc: String,
        badge: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val req = CreatePackageRequest(id, title, desc, badge)
            val result = repository.createPackage(req)
            result.onSuccess {
                loadPackages(_searchQuery.value)
                onSuccess()
            }.onFailure { err ->
                onError(err.message ?: "Gagal membuat paket baru.")
            }
        }
    }
}

