package com.example.a0100_toefleptapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.a0100_toefleptapp.util.ToeflAudioPlayer
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

    val context = androidx.compose.ui.platform.LocalContext.current
    val audioPlayer = remember { ToeflAudioPlayer(context) }
    val isAudioPlaying by audioPlayer.isPlaying.collectAsState()
    val speechRate by audioPlayer.speechRate.collectAsState()
    var showTranscript by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }

    LaunchedEffect((uiState as? TestUiState.InProgress)?.currentQuestionIndex) {
        audioPlayer.stop()
        showTranscript = false
    }

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

                if (questions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚠️ Data Soal Belum Tersedia",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Sedang memuat atau server belum mengirimkan butir soal paket ini.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadTestPackage(packageId) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Muat Ulang Soal")
                                }
                            }
                        }
                    }
                } else {
                    val p1Count = questions.count { it.part == 1 }
                    val p2Count = questions.count { it.part == 2 }
                    val p3Count = questions.count { it.part == 3 }
                    val partQuestions = questions.filter { it.part == state.currentPart }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        // 1. PINNED PART TABS (PrimaryTabRow)
                        PrimaryTabRow(
                            selectedTabIndex = (state.currentPart - 1).coerceIn(0, 2),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            val partTabs = listOf(
                                1 to "P1: Listening ($p1Count)",
                                2 to "P2: Structure ($p2Count)",
                                3 to "P3: Reading ($p3Count)"
                            )
                            partTabs.forEach { (p, label) ->
                                Tab(
                                    selected = state.currentPart == p,
                                    onClick = { viewModel.switchPart(p) },
                                    text = {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (state.currentPart == p) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        // 2. PINNED QUESTION PALETTE
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Daftar Nomor Part ${state.currentPart}:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${state.userAnswers.size} / ${questions.size} Terjawab",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(partQuestions) { q ->
                                        val isCurrent = currentQuestion?.id == q.id
                                        val isAnswered = state.userAnswers.containsKey(q.id)
                                        val globalIdx = questions.indexOfFirst { it.id == q.id }

                                        val chipBg = when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isAnswered -> Color(0xFF10B981)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                        val textColor = if (isCurrent || isAnswered) Color.White else Color.Black

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = chipBg,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clickable { viewModel.jumpToQuestion(globalIdx) }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${q.number}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                        // 3. SCROLLABLE QUESTION CONTENT
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            if (currentQuestion != null) {
                                val currentInPart = partQuestions.indexOfFirst { it.id == currentQuestion.id } + 1
                                val totalInPart = partQuestions.size

                                // Question Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Soal No. $currentInPart dari $totalInPart (Part ${state.currentPart})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = currentQuestion.type.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

                                // Audio Player Card for Part 1 Listening
                                if (!currentQuestion.audio_script.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isAudioPlaying) Color(0xFF10B981) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.Headphones,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Audio Listening (Part 1)",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }

                                                if (isAudioPlaying) {
                                                    Surface(
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                Icons.Default.GraphicEq,
                                                                contentDescription = null,
                                                                tint = Color(0xFF047857),
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = "Memutar Suara...",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF047857)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Play / Stop Controls
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(
                                                    onClick = {
                                                        if (isAudioPlaying) {
                                                            audioPlayer.stop()
                                                        } else {
                                                            audioPlayer.playAudio(currentQuestion.audio_script)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isAudioPlaying) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        if (isAudioPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                        contentDescription = null
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isAudioPlaying) "Hentikan Audio" else "▶️ Putar Audio Percakapan",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                OutlinedButton(
                                                    onClick = { audioPlayer.toggleSpeed() },
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text(
                                                        text = "${speechRate}x",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Toggle Transcript visibility (Exam Mode vs Study Mode)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { showTranscript = !showTranscript }
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (showTranscript) "Sembunyikan Transkrip Teks" else "Tampilkan Transkrip Teks (Bantuan Belajar)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Icon(
                                                    if (showTranscript) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            if (showTranscript) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.White,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = currentQuestion.audio_script,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.DarkGray,
                                                        modifier = Modifier.padding(10.dp),
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "💡 Di ujian resmi TOEFL EPT, teks audio tidak ditampilkan. Dengarkan suara audio dengan seksama!",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
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

                                // Options A, B, C, D (Filter out blank options)
                                val selectedAns = state.userAnswers[currentQuestion.id]

                                val rawOptions = listOf(
                                    "a" to currentQuestion.option_a,
                                    "b" to currentQuestion.option_b,
                                    "c" to currentQuestion.option_c,
                                    "d" to currentQuestion.option_d
                                )
                                val options = rawOptions.filter { it.second.isNotBlank() }

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

