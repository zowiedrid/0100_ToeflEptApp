package com.example.a0100_toefleptapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.a0100_toefleptapp.model.Question
import com.example.a0100_toefleptapp.viewmodel.TestUiState
import com.example.a0100_toefleptapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSessionScreen(
    packageId: String,
    viewModel: TestViewModel,
    onTestFinished: (String) -> Unit, // returns attemptId
    onExit: () -> Unit
) {
    LaunchedEffect(packageId) {
        viewModel.loadTestPackage(packageId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var showConfirmSubmit by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is TestUiState.Submitted) {
            val attempt = (uiState as TestUiState.Submitted).attempt
            onTestFinished(attempt.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is TestUiState.InProgress -> {
                            val mins = state.timeRemainingSeconds / 60
                            val secs = state.timeRemainingSeconds % 60
                            val timerText = String.format("%02d:%02d", mins, secs)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Part ${state.currentPart}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (mins < 5) Color(0xFFEF4444) else Color(0xFF1E3A8A)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = timerText,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        else -> Text("Sesi Ujian TOEFL EPT", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            if (uiState is TestUiState.InProgress) {
                val state = uiState as TestUiState.InProgress
                val total = state.pkg.questions?.size ?: 0
                val isLast = state.currentQuestionIndex >= total - 1

                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.goToPreviousQuestion() },
                            enabled = state.currentQuestionIndex > 0,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sebelumnya")
                        }

                        if (isLast) {
                            Button(
                                onClick = { showConfirmSubmit = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Selesai & Kumpulkan", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.goToNextQuestion() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Selanjutnya")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is TestUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TestUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTestPackage(packageId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            is TestUiState.InProgress -> {
                val questions = state.pkg.questions ?: emptyList()
                val currentQuestion: Question? = questions.getOrNull(state.currentQuestionIndex)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Part Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1 to "Part 1: Listening", 2 to "Part 2: Structure", 3 to "Part 3: Reading").forEach { (p, label) ->
                            FilterChip(
                                selected = state.currentPart == p,
                                onClick = { viewModel.switchPart(p) },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentQuestion != null) {
                        // Question Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soal No. ${currentQuestion.number} dari ${questions.size}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.LightGray.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = currentQuestion.type.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Reading passage if any
                        if (!currentQuestion.passage_text.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "📖 Bacaan Terkait:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.passage_text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        // Audio script note for Part 1 Listening
                        if (!currentQuestion.audio_script.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Audio Transcript: \"${currentQuestion.audio_script}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Prompt
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = currentQuestion.prompt,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options A, B, C, D
                        val selectedAns = state.userAnswers[currentQuestion.id]

                        val options = listOf(
                            "a" to currentQuestion.option_a,
                            "b" to currentQuestion.option_b,
                            "c" to currentQuestion.option_c,
                            "d" to currentQuestion.option_d
                        )

                        options.forEach { (key, text) ->
                            val isSelected = selectedAns == key
                            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.selectAnswer(currentQuestion.id, key) }
                                    .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                color = bgColor
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = key.uppercase(),
                                                color = if (isSelected) Color.White else Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            else -> {}
        }
    }

    if (showConfirmSubmit) {
        AlertDialog(
            onDismissRequest = { showConfirmSubmit = false },
            title = { Text("Kumpulkan Lembar Jawaban?") },
            text = { Text("Apakah Anda yakin ingin menyelesaikan tes dan melihat penilaian skor TOEFL EPT Anda?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSubmit = false
                        viewModel.submitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Ya, Kumpulkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSubmit = false }) { Text("Kembali Periksa") }
            }
        )
    }
}

