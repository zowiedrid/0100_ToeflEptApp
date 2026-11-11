package com.example.a0100_toefleptapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.util.CertificateFileInfo
import com.example.a0100_toefleptapp.util.CertificateFileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CertificateUiState {
    object Idle : CertificateUiState()
    data class SavedSuccess(val filePath: String) : CertificateUiState()
    data class Error(val message: String) : CertificateUiState()
}

class CertificateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CertificateUiState>(CertificateUiState.Idle)
    val uiState: StateFlow<CertificateUiState> = _uiState.asStateFlow()

    private val _savedFiles = MutableStateFlow<List<CertificateFileInfo>>(emptyList())
    val savedFiles: StateFlow<List<CertificateFileInfo>> = _savedFiles.asStateFlow()

    private val _openedFileContent = MutableStateFlow<String?>(null)
    val openedFileContent: StateFlow<String?> = _openedFileContent.asStateFlow()

    fun loadSavedFiles(context: Context) {
        viewModelScope.launch {
            val list = CertificateFileManager.listSavedCertificates(context)
            _savedFiles.value = list
        }
    }

    fun saveCertificate(
        context: Context,
        attempt: TestAttempt,
        nama: String,
        nim: String
    ) {
        viewModelScope.launch {
            val result = CertificateFileManager.saveCertificate(context, attempt, nama, nim)
            result.onSuccess { path ->
                _uiState.value = CertificateUiState.SavedSuccess(path)
                loadSavedFiles(context)
            }.onFailure { err ->
                _uiState.value = CertificateUiState.Error(err.message ?: "Gagal menyimpan berkas.")
            }
        }
    }

    fun openFile(filePath: String) {
        val text = CertificateFileManager.readCertificateText(filePath)
        _openedFileContent.value = text
    }

    fun closeOpenedFile() {
        _openedFileContent.value = null
    }

    fun deleteFile(context: Context, filePath: String) {
        CertificateFileManager.deleteCertificate(filePath)
        loadSavedFiles(context)
        if (_openedFileContent.value != null) {
            _openedFileContent.value = null
        }
    }
}

