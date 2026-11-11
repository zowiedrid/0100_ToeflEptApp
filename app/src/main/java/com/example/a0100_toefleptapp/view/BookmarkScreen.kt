package com.example.a0100_toefleptapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a0100_toefleptapp.model.Bookmark
import com.example.a0100_toefleptapp.viewmodel.BookmarkUiState
import com.example.a0100_toefleptapp.viewmodel.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var editingBookmark by remember { mutableStateOf<Bookmark?>(null) }
    var editNoteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan & Soal Tersimpan", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Bar (CRUDS Search)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchChanged(it) },
                placeholder = { Text("Cari catatan atau butir soal...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (val state = uiState) {
                is BookmarkUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BookmarkUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is BookmarkUiState.Success -> {
                    if (state.bookmarks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada soal yang disimpan.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 60.dp)
                        ) {
                            items(state.bookmarks) { b ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ID: ${b.question_id}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            Row {
                                                IconButton(onClick = {
                                                    editingBookmark = b
                                                    editNoteText = b.notes ?: ""
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Catatan", modifier = Modifier.size(18.dp))
                                                }

                                                IconButton(onClick = { viewModel.deleteBookmark(b.id) }) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }

                                        if (b.question != null) {
                                            Text(
                                                text = b.question.prompt,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Kunci: ${b.question.correct_answer.uppercase()} | ${b.question.explanation ?: ""}",
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )
                                        }

                                        if (!b.notes.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFFEF3C7)
                                            ) {
                                                Text(
                                                    text = "💡 Catatan Saya: ${b.notes}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF92400E),
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Edit Catatan (CRUDS Update)
    if (editingBookmark != null) {
        AlertDialog(
            onDismissRequest = { editingBookmark = null },
            title = { Text("Edit Catatan Belajar") },
            text = {
                OutlinedTextField(
                    value = editNoteText,
                    onValueChange = { editNoteText = it },
                    label = { Text("Catatan Refleksi Pribadi") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateNote(editingBookmark!!.id, editNoteText)
                    editingBookmark = null
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBookmark = null }) { Text("Batal") }
            }
        )
    }
}

