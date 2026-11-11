package com.example.a0100_toefleptapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a0100_toefleptapp.repository.*
import com.example.a0100_toefleptapp.service.NetworkClient
import com.example.a0100_toefleptapp.ui.theme.ToeflEptTheme
import com.example.a0100_toefleptapp.util.TokenManager
import com.example.a0100_toefleptapp.view.uicontroller.Navigasi
import com.example.a0100_toefleptapp.viewmodel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = NetworkClient.getApiService(applicationContext)
        val tokenManager = TokenManager(applicationContext)

        val authRepo = AuthRepository(apiService, tokenManager)
        val testRepo = TestRepository(apiService)
        val historyRepo = HistoryRepository(apiService)
        val bookmarkRepo = BookmarkRepository(apiService)

        val authViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepo) as T
            }
        })[AuthViewModel::class.java]

        val homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(testRepo) as T
            }
        })[HomeViewModel::class.java]

        val testViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TestViewModel(testRepo) as T
            }
        })[TestViewModel::class.java]

        val historyViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(historyRepo) as T
            }
        })[HistoryViewModel::class.java]

        val bookmarkViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BookmarkViewModel(bookmarkRepo) as T
            }
        })[BookmarkViewModel::class.java]

        val certViewModel = ViewModelProvider(this)[CertificateViewModel::class.java]

        setContent {
            ToeflEptTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigasi(
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        testViewModel = testViewModel,
                        historyViewModel = historyViewModel,
                        bookmarkViewModel = bookmarkViewModel,
                        certViewModel = certViewModel
                    )
                }
            }
        }
    }
}

