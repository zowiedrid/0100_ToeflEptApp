package com.example.a0100_toefleptapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TenseInfo(
    val id: String,
    val name: String,
    val formula: String,
    val timeSignals: String,
    val verbTransform: (subject: String, verb: String, complement: String) -> Pair<String, String>, // returns (prefix, highlightedVerb, suffix)
    val toeflTip: String,
    val sampleExamQuestion: String
)

data class SentenceScenario(
    val id: String,
    val label: String,
    val subject: String,
    val baseVerb: String,
    val complement: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TensesLabScreen(
    onBack: () -> Unit
) {
    val scenarios = remember {
        listOf(
            SentenceScenario(
                id = "s1",
                label = "Maria / Write Thesis",
                subject = "Maria",
                baseVerb = "write",
                complement = "her graduation thesis for the bachelor's degree at UMY."
            ),
            SentenceScenario(
                id = "s2",
                label = "Scientists / Publish Research",
                subject = "The researchers",
                baseVerb = "publish",
                complement = "breakthrough findings on renewable solar cells."
            ),
            SentenceScenario(
                id = "s3",
                label = "Students / Attend TOEFL Prep",
                subject = "The undergraduate students",
                baseVerb = "attend",
                complement = "intensive TOEFL EPT preparation classes."
            )
        )
    }

    val tenses = remember {
        listOf(
            TenseInfo(
                id = "simple_present",
                name = "Simple Present",
                formula = "S + V1 / V1(-s/-es) + O",
                timeSignals = "always, usually, every day, often, generally, scientific facts",
                verbTransform = { subj, verb, comp ->
                    val isSingular = subj == "Maria"
                    val v = if (isSingular) "${verb}s" else verb
                    "$subj " to "$v $comp"
                },
                toeflTip = "Gunakan untuk fakta ilmiah umum atau kebiasaan tetap. Sering diuji pada kesesuaian Subject-Verb (tunggal vs jamak).",
                sampleExamQuestion = "Every semester, Maria (writes / writing) her academic journal reports on time."
            ),
            TenseInfo(
                id = "present_continuous",
                name = "Present Continuous",
                formula = "S + am/is/are + V-ing + O",
                timeSignals = "now, right now, at present, currently, look!, listen!",
                verbTransform = { subj, verb, comp ->
                    val be = if (subj == "Maria") "is" else "are"
                    val v = if (verb == "write") "writing" else "${verb}ing"
                    "$subj " to "$be $v $comp"
                },
                toeflTip = "Menyatakan aktivitas yang sedang berlangsung detik ini. Ingat: kata kerja statif (seperti know, believe, prefer) tidak boleh V-ing!",
                sampleExamQuestion = "Currently, the researchers (are publishing / publish) their newest quarterly findings."
            ),
            TenseInfo(
                id = "present_perfect",
                name = "Present Perfect",
                formula = "S + have/has + V3 + O",
                timeSignals = "since, for (durasi), already, yet, recently, so far",
                verbTransform = { subj, verb, comp ->
                    val haveHas = if (subj == "Maria") "has" else "have"
                    val v3 = when (verb) {
                        "write" -> "written"
                        "publish" -> "published"
                        else -> "attended"
                    }
                    "$subj " to "$haveHas $v3 $comp"
                },
                toeflTip = "Sangat sering keluar di TOEFL EPT! Ciri khas mutlak: jika ada kata 'since + titik waktu lampau' (misal: since 2020), wajib pilih Present Perfect!",
                sampleExamQuestion = "Since last month, Maria (has written / wrote) five preliminary draft chapters."
            ),
            TenseInfo(
                id = "present_perfect_cont",
                name = "Present Perfect Cont.",
                formula = "S + have/has been + V-ing + O",
                timeSignals = "these days, all morning, lately, for hours continuously",
                verbTransform = { subj, verb, comp ->
                    val haveHas = if (subj == "Maria") "has" else "have"
                    val v = if (verb == "write") "writing" else "${verb}ing"
                    "$subj " to "$haveHas been $v $comp"
                },
                toeflTip = "Menekankan durasi proses yang sudah dimulai di masa lampau dan MASIH sedang berlangsung tanpa henti hingga sekarang.",
                sampleExamQuestion = "Maria looks tired these days because she (has been writing / wrote) all night."
            ),
            TenseInfo(
                id = "simple_past",
                name = "Simple Past",
                formula = "S + V2 + O",
                timeSignals = "yesterday, last year, in 1995, two days ago, formerly",
                verbTransform = { subj, verb, comp ->
                    val v2 = when (verb) {
                        "write" -> "wrote"
                        "publish" -> "published"
                        else -> "attended"
                    }
                    "$subj " to "$v2 $comp"
                },
                toeflTip = "Kejadian yang sudah tuntas di masa lampau pada titik waktu spesifik. Waspadai kata kerja tak beraturan (irregular verbs) seperti write -> wrote.",
                sampleExamQuestion = "Two weeks ago, the researchers (published / publish) their peer-reviewed paper."
            ),
            TenseInfo(
                id = "past_continuous",
                name = "Past Continuous",
                formula = "S + was/were + V-ing + O",
                timeSignals = "when + Simple Past, while + Past Continuous, at 8 PM yesterday",
                verbTransform = { subj, verb, comp ->
                    val wasWere = if (subj == "Maria") "was" else "were"
                    val v = if (verb == "write") "writing" else "${verb}ing"
                    "$subj " to "$wasWere $v $comp"
                },
                toeflTip = "Sering berpasangan dengan klausa 'when + V2'. Menandakan aktivitas yang sedang terjadi saat diinterupsi kejadian lampau lain.",
                sampleExamQuestion = "Maria (was writing / wrote) her thesis when the power suddenly went out."
            ),
            TenseInfo(
                id = "past_perfect",
                name = "Past Perfect",
                formula = "S + had + V3 + O",
                timeSignals = "before + Simple Past, after, by the time, wish / if only (penyesalan)",
                verbTransform = { subj, verb, comp ->
                    val v3 = when (verb) {
                        "write" -> "written"
                        "publish" -> "published"
                        else -> "attended"
                    }
                    "$subj " to "had $v3 $comp"
                },
                toeflTip = "Kejadian yang terjadi LEBIH DULU daripada kejadian lampau lainnya. Kunci: 'By the time + V2, S + had + V3'.",
                sampleExamQuestion = "By the time the professor arrived, Maria (had written / wrote) the entire proposal."
            ),
            TenseInfo(
                id = "future_simple",
                name = "Simple Future (Will)",
                formula = "S + will + V1 (murni) + O",
                timeSignals = "tomorrow, next week, soon, in the future, predict",
                verbTransform = { subj, verb, comp ->
                    "$subj " to "will $verb $comp"
                },
                toeflTip = "Setelah modal auxiliary 'will', kata kerja WAJIB kembali ke bentuk dasar V1 murni (tanpa s/es, tanpa ed, tanpa ing).",
                sampleExamQuestion = "The committee expects that all students (will attend / will attending) the graduation."
            )
        )
    }

    var selectedScenarioId by remember { mutableStateOf("s1") }
    var selectedTenseId by remember { mutableStateOf("present_perfect") }

    val currentScenario = scenarios.first { it.id == selectedScenarioId }
    val currentTense = tenses.first { it.id == selectedTenseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratorium Tenses EPT", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intro Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Pilih kalimat skenario & klik tombol tense untuk melihat perubahan bentuk kata kerja secara dinamis!",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // Scenario Selector
            Text("1. Pilih Kalimat Skenario:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenarios.forEach { s ->
                    val isSel = s.id == selectedScenarioId
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedScenarioId = s.id }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            // Tenses Chips
            Text("2. Klik Tense yang Ingin Diuji:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tenses) { t ->
                    val isSel = t.id == selectedTenseId
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9),
                        border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.clickable { selectedTenseId = t.id }
                    ) {
                        Text(
                            text = t.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Dynamic Result Card
            val (prefix, verbRest) = currentTense.verbTransform(
                currentScenario.subject,
                currentScenario.baseVerb,
                currentScenario.complement
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "HASIL PERUBAHAN: ${currentTense.name.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val annotatedText = buildAnnotatedString {
                        append(prefix)
                        withStyle(style = SpanStyle(color = Color(0xFF1E40AF), fontWeight = FontWeight.ExtraBold)) {
                            append(verbRest)
                        }
                    }

                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Formula
                    Text("📐 Rumus Baku (Formula):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentTense.formula,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Signals
                    Text("⏰ Penanda Waktu Kunci (Time Signals):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentTense.timeSignals,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // TOEFL EPT Tip
                    Text("🎯 Trik Menjawab di Soal EPT:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD97706))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentTense.toeflTip,
                        fontSize = 13.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sample Exam
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📝 Contoh Soal Terkait:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(currentTense.sampleExamQuestion, fontSize = 12.sp, color = Color(0xFF7F1D1D))
                        }
                    }
                }
            }
        }
    }
}
