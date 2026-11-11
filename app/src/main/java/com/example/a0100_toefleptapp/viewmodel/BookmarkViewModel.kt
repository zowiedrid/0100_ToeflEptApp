package com.example.a0100_toefleptapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.Bookmark
import com.example.a0100_toefleptapp.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BookmarkUiState {
    object Loading : BookmarkUiState()
    data class Success(val bookmarks: List<Bookmark>) : BookmarkUiState()
    data class Error(val message: String) : BookmarkUiState()
}

class BookmarkViewModel(private val repository: BookmarkRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks(query: String? = null) {
        _uiState.value = BookmarkUiState.Loading
        viewModelScope.launch {
            val result = repository.getBookmarks(query)
            result.onSuccess { list ->
                _uiState.value = BookmarkUiState.Success(list)
            }.onFailure { err ->
                _uiState.value = BookmarkUiState.Error(err.message ?: "Gagal memuat bookmark.")
            }
        }
    }

    fun onSearchChanged(newQuery: String) {
        _searchQuery.value = newQuery
        loadBookmarks(if (newQuery.isBlank()) null else newQuery)
    }

    fun updateNote(id: Int, newNote: String) {
        viewModelScope.launch {
            val result = repository.updateNote(id, newNote)
            result.onSuccess {
                loadBookmarks(_searchQuery.value)
            }
        }
    }

    fun deleteBookmark(id: Int) {
        viewModelScope.launch {
            val result = repository.deleteBookmark(id)
            result.onSuccess {
                loadBookmarks(_searchQuery.value)
            }
        }
    }
}

