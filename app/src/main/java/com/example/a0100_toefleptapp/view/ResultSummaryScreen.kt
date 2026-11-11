package com.example.a0100_toefleptapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.model.User
import com.example.a0100_toefleptapp.viewmodel.CertificateUiState
import com.example.a0100_toefleptapp.viewmodel.CertificateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultSummaryScreen(
    attempt: TestAttempt,
    currentUser: User?,
    certViewModel: CertificateViewModel,
    onBackToHome: () -> Unit,
    onViewCertificates: () -> Unit
) {
    val context = LocalContext.current
    val certState by certViewModel.uiState.collectAsState()

    LaunchedEffect(certState) {
        if (certState is CertificateUiState.SavedSuccess) {
            Toast.makeText(context, "Sertifikat berhasil disimpan ke penyimpanan lokal!", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Skor TOEFL EPT", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Hasil Utama
            val isPassed = attempt.is_passed
            val statusColor = if (isPassed) Color(0xFF16A34A) else Color(0xFFDC2626)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = if (isPassed) "🎉" else "📚", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isPassed) "LULUS PERSYARATAN SKRIPSI" else "BELUM MEMENUHI SYARAT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skor Raksasa
                    Text(
                        text = "${attempt.total_score}",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Skala Resmi TOEFL ITP (310 – 677)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (attempt.score_gap >= 0) "Surplus +${attempt.score_gap} dari cutoff 450" else "Kurang ${attempt.score_gap} dari cutoff 450",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rincian 3 Bagian
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rincian Skor Per Bagian",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ScoreSectionRow("Part 1: Listening", attempt.listening_scaled, attempt.listening_raw, attempt.listening_total)
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    ScoreSectionRow("Part 2: Structure", attempt.structure_scaled, attempt.structure_raw, attempt.structure_total)
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    ScoreSectionRow("Part 3: Reading", attempt.reading_scaled, attempt.reading_raw, attempt.reading_total)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan ke File Storage Lokal (Kategori 9 Pedoman PAM)
            Button(
                onClick = {
                    certViewModel.saveCertificate(
                        context = context,
                        attempt = attempt,
                        nama = currentUser?.nama_lengkap ?: "Mahasiswa UMY",
                        nim = currentUser?.nim ?: "20200140100"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Sertifikat ke File Lokal", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tombol Buka File Storage Lokal
            OutlinedButton(
                onClick = onViewCertificates,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Folder, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka Berkas Sertifikat Tersimpan")
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke Beranda Latihan")
            }
        }
    }
}

@Composable
fun ScoreSectionRow(title: String, scaledScore: Int, rawCorrect: Int, rawTotal: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = "Benar $rawCorrect dari $rawTotal soal", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = "$scaledScore",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

