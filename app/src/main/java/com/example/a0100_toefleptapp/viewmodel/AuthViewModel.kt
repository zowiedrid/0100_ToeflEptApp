package com.example.a0100_toefleptapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.User
import com.example.a0100_toefleptapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkLoginStatus(): Boolean = repository.isLoggedIn()

    fun getCurrentUser(): User? = repository.getCurrentUser()

    fun login(emailOrNim: String, pass: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.login(emailOrNim, pass)
            result.onSuccess { data ->
                _uiState.value = AuthUiState.Success(data.user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Login gagal.")
            }
        }
    }

    fun register(nama: String, nim: String, email: String, pass: String, role: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.register(nama, nim, email, pass, role)
            result.onSuccess { data ->
                _uiState.value = AuthUiState.Success(data.user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Registrasi gagal.")
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

