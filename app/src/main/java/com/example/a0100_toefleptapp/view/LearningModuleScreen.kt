package com.example.a0100_toefleptapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningModuleScreen(
    onBack: () -> Unit,
    onOpenTensesLab: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(1) } // 0 = Part 1, 1 = Part 2, 2 = Part 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modul Belajar & Trik EPT", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenTensesLab) {
                        Icon(Icons.Default.Tune, contentDescription = "Lab Tenses", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pinned Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Part 1: Listening", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Part 2: Structure", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Part 3: Reading", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> Part1ListeningContent()
                    1 -> Part2StructureContent(onOpenTensesLab = onOpenTensesLab)
                    2 -> Part3ReadingContent()
                }
            }
        }
    }
}

@Composable
fun Part1ListeningContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎧 Strategi Utama Part 1: Listening Comprehension", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Part 1 terdiri dari Short Dialogues (percakapan pendek), Long Conversations, dan Mini Lectures. Target minimal untuk skor 450 adalah menjawab benar minimal 22 dari 40 soal.", fontSize = 13.sp, color = Color.DarkGray)
        }
    }

    RuleCard(
        title = "1. Fokus Selalu pada Pembicara Kedua (Last Speaker)",
        badge = "Kunci 80% Soal",
        description = "Pada percakapan pendek dua orang, inti pesan atau jawaban atas pertanyaan narator hampir selalu berada pada ucapan pembicara kedua. Pembicara pertama hanya memberikan pengantar konteks.",
        example = "Man: Do you think Professor Clark will accept late papers?\nWoman: In all semesters I've known him, he's never taken anything late.\nNarrator: What does the woman imply?\n-> Kunci: Dengarkan penolakan tegas dari wanita di baris kedua!"
    )

    RuleCard(
        title = "2. Waspadai Jebakan Kata Bunyi Mirip (Sound-Alike Traps)",
        badge = "Jebakan Paling Sering",
        description = "Pilihan jawaban yang kata-katanya berbunyi paling mirip dengan yang terdengar di audio justru 90% adalah jebakan (distractor). Carilah sinonim makna (paraphrase), bukan bunyi kata yang sama.",
        example = "Audio: \"The department decided to hire more staff.\"\nJebakan: A. \"They decided to fire...\" (fire mirip hire -> SALAH!)\nJawaban Benar: B. \"They chose to recruit new workers.\" (sinonim)"
    )

    RuleCard(
        title = "3. Kuasai Makna Ungkapan Idiomatis & Phrasal Verbs",
        badge = "Kosakata Kunci",
        description = "Ekspresi sehari-hari yang sering diuji di EPT:\n• 'Under the weather' = merasa kurang sehat / sakit\n• 'Call it a day' = menyudahi pekerjaan hari ini\n• 'Piece of cake' = sangat mudah\n• 'Run out of' = kehabisan persediaan\n• 'Put off' = menunda (postpone)"
    )
}

@Composable
fun Part2StructureContent(onOpenTensesLab: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Laboratorium Tenses Dinamis", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                Text("Uji coba perubahan kalimat secara langsung dengan 1 klik!", fontSize = 12.sp, color = Color(0xFFB45309))
            }
            Button(
                onClick = onOpenTensesLab,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buka Lab", fontSize = 12.sp)
            }
        }
    }

    RuleCard(
        title = "1. Pasangan Wajib (Collocation & Correlative)",
        badge = "Selesai 3 Detik",
        description = "Banyak soal Structure langsung selesai jika tahu pasangan preposisinya:\n" +
                "• aware -> OF (bukan with / about)\n" +
                "• worried -> ABOUT\n" +
                "• impact / effect -> ON\n" +
                "• reason -> FOR\n" +
                "• increase / decrease -> BY (selisih persentase: by 20%)\n" +
                "• prefer [X] -> TO [Y] (bukan than)\n" +
                "• the same -> AS (bukan as same as)\n" +
                "• so + Adj -> THAT + S + V"
    )

    RuleCard(
        title = "2. Bedakan Konektor: Klausa vs Frasa Benda",
        badge = "Sering Salah",
        description = "Cek apakah setelah kata hubung ada [Subjek + Verb] atau hanya [Kata Benda]:\n\n" +
                "SEBAB:\n" +
                "• Because / Since / As + [Subjek + Kata Kerja]\n" +
                "• Due to / Because of + [Frasa Benda / Noun Phrase]\n\n" +
                "PERTENTANGAN:\n" +
                "• Although / Even though + [Subjek + Kata Kerja]\n" +
                "• In spite of / Despite + [Frasa Benda / Noun Phrase]",
        example = "Soal Error Recognition:\n" +
                "\"Despite (A) she was exhausted after (B) the exam, Maria (C) continued working on her (D) project.\"\n" +
                "-> Salah pada (A) 'Despite' karena setelahnya ada klausa utuh ('she was exhausted'). Seharusnya gunakan 'Although'!"
    )

    RuleCard(
        title = "3. Trik Participle & Reduced Clauses",
        badge = "Pola Lanjutan",
        description = "Jika kalimat sudah memiliki Subjek dan Kata Kerja utama, jangan tambahkan kata kerja biasa lagi tanpa kata sambung:\n\n" +
                "• Bermakna Pasif: Gunakan V3 (Past Participle)\n" +
                "Contoh: \"Regular exercise, combined with a healthy diet, improves focus.\"\n\n" +
                "• Bermakna Aktif / Akibat Langsung: Gunakan V-ing (Present Participle)\n" +
                "Contoh: \"Students living in dormitory must adhere to rules.\""
    )

    RuleCard(
        title = "4. Bentuk Kata Kerja Setelah Kata Tanya / Saran",
        badge = "Trik Soal Ajakan",
        description = "Lihat bentuk kata kerja setelah titik-titik:\n" +
                "• Jika diikuti V-ing -> Pilih 'How about' / 'What about' (Contoh: How about submitting the paper today?)\n" +
                "• Jika diikuti V1 murni (base) -> Pilih 'Why don't we' / 'Shall we' / 'Let's' (Contoh: Why don't we submit the paper today?)"
    )
}

@Composable
fun Part3ReadingContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📖 Strategi Membaca Cepat Part 3", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Part 3 menguji pemahaman 4-5 teks akademik dalam waktu 55 menit. Kunci utama adalah manajemen waktu: jangan membaca kata demi kata dari awal!", fontSize = 13.sp, color = Color.DarkGray)
        }
    }

    RuleCard(
        title = "1. Teknik Skimming untuk Ide Pokok (Main Idea)",
        badge = "Waktu Hemat 50%",
        description = "Untuk pertanyaan 'What is the main idea of the passage?' atau 'The author's primary purpose is...':\n" +
                "• Baca 1-2 kalimat pertama di Paragraf 1 (Topic Sentence).\n" +
                "• Baca kalimat pertama di setiap paragraf berikutnya.\n" +
                "• Baca kalimat terakhir di paragraf penutup (Kesimpulan)."
    )

    RuleCard(
        title = "2. Teknik Scanning untuk Detail Pertanyaan Tertentu",
        badge = "Cari Kata Kunci",
        description = "Untuk pertanyaan spesifik seperti tahun, nama tokoh, lokasi, atau istilah ilmiah:\n" +
                "• Ambil 1 kata kunci khas dari pertanyaan (misal nama 'Professor Watson' atau tahun '1920').\n" +
                "• Gerakkan mata secara diagonal melintasi teks untuk menemukan kata kunci tersebut.\n" +
                "• Baca dengan teliti 1 kalimat sebelum dan 1 kalimat sesudah kata kunci ditemukan."
    )

    RuleCard(
        title = "3. Pertanyaan Sinonim Kontekstual (Vocabulary in Context)",
        badge = "Lihat Konteks",
        description = "Pertanyaan 'The word X in line Y is closest in meaning to...':\n" +
                "Jangan hanya mengandalkan hafalan kamus, karena kata bahasa Inggris memiliki banyak arti tergantung konteks kalimat. Masukkan pilihan jawaban satu per satu ke dalam kalimat asli dan lihat mana yang paling masuk akal."
    )
}

@Composable
fun RuleCard(
    title: String,
    badge: String,
    description: String,
    example: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            if (!example.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Contoh Soal & Pembahasan:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = example,
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
