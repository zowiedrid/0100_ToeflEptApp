package com.example.a0100_toefleptapp.util

import android.content.Context
import com.example.a0100_toefleptapp.model.TestAttempt
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CertificateFileInfo(
    val fileName: String,
    val filePath: String,
    val fileSizeFormatted: String,
    val lastModifiedFormatted: String
)

object CertificateFileManager {

    private const val CERTIFICATE_DIR = "toefl_certificates"

    private fun getCertDir(context: Context): File {
        val dir = File(context.filesDir, CERTIFICATE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * SIMPAN FILE LOKAL (Memenuhi Kategori 9 Pedoman PAM)
     * Menulis berkas laporan prediksi skor TOEFL EPT ke storage internal HP.
     */
    fun saveCertificate(
        context: Context,
        attempt: TestAttempt,
        namaMahasiswa: String,
        nimMahasiswa: String
    ): Result<String> {
        return try {
            val dir = getCertDir(context)
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Sertifikat_TOEFL_${nimMahasiswa}_${attempt.package_id}_$dateStr.txt"
            val file = File(dir, fileName)

            val statusKelulusan = if (attempt.is_passed) {
                "LULUS SYARAT SIDANG SKRIPSI (>= 450)"
            } else {
                "BELUM MEMENUHI SYARAT SIDANG SKRIPSI (< 450)"
            }

            val content = buildString {
                appendLine("==================================================================")
                appendLine("     UNIVERSITAS MUHAMMADIYAH YOGYAKARTA")
                appendLine("     LAPORAN RESMI PREDIKSI SKOR TOEFL EPT (ENGLISH PROFICIENCY TEST)")
                appendLine("==================================================================")
                appendLine()
                appendLine("DATA PESERTA:")
                appendLine("  Nama Mahasiswa   : $namaMahasiswa")
                appendLine("  NIM              : $nimMahasiswa")
                appendLine("  Paket Ujian      : ${attempt.package_title}")
                appendLine("  Tanggal Ujian    : ${attempt.attempt_date ?: SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID")).format(Date())}")
                appendLine("  Durasi Tes       : ${attempt.time_spent_seconds / 60} Menit ${attempt.time_spent_seconds % 60} Detik")
                appendLine()
                appendLine("RINCIAN SKOR SKALA RESMI (310 - 677):")
                appendLine("  1. Section 1 (Listening Comprehension)   : ${attempt.listening_scaled}  (Benar: ${attempt.listening_raw}/${attempt.listening_total})")
                appendLine("  2. Section 2 (Structure & Written Expr.) : ${attempt.structure_scaled}  (Benar: ${attempt.structure_raw}/${attempt.structure_total})")
                appendLine("  3. Section 3 (Reading Comprehension)    : ${attempt.reading_scaled}  (Benar: ${attempt.reading_raw}/${attempt.reading_total})")
                appendLine("  ---------------------------------------------------------------")
                appendLine("  TOTAL SKOR PREDIKSI                     : ${attempt.total_score}")
                appendLine("  STANDAR KELULUSAN KAMPUS (CUTOFF)       : 450")
                appendLine("  SELISIH SKOR (GAP)                      : ${if (attempt.score_gap >= 0) "+${attempt.score_gap}" else "${attempt.score_gap}"}")
                appendLine("  STATUS HASIL                            : $statusKelulusan")
                appendLine()
                appendLine("CATATAN EVALUASI:")
                if (attempt.is_passed) {
                    appendLine("  Selamat! Skor Anda telah melampaui standar minimal 450.")
                    appendLine("  Laporan ini dapat digunakan sebagai lampiran persyaratan tugas akhir.")
                } else {
                    appendLine("  Tingkatkan latihan pada Section 2 (Structure) dan Section 3 (Reading).")
                    appendLine("  Pelajari kembali modul Error Identification dan Pasangan Kolokasi di aplikasi.")
                }
                appendLine()
                appendLine("==================================================================")
                appendLine("Dokumen ini diterbitkan secara otomatis oleh Aplikasi TOEFL EPT Prep (0100)")
                appendLine("ID Verifikasi: ${attempt.id}")
                appendLine("==================================================================")
            }

            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * BACA FILE LOKAL (Memenuhi Kategori 9 Pedoman PAM)
     * Mengambil daftar seluruh file sertifikat yang pernah disimpan.
     */
    fun listSavedCertificates(context: Context): List<CertificateFileInfo> {
        val dir = getCertDir(context)
        val files = dir.listFiles { f -> f.isFile && f.name.endsWith(".txt") } ?: return emptyList()

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

        return files.sortedByDescending { it.lastModified() }.map { file ->
            CertificateFileInfo(
                fileName = file.name,
                filePath = file.absolutePath,
                fileSizeFormatted = "${file.length() / 1024 + 1} KB",
                lastModifiedFormatted = dateFormat.format(Date(file.lastModified()))
            )
        }
    }

    /**
     * BACA ISI KONTEN FILE LOKAL
     */
    fun readCertificateText(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return "Berkas tidak ditemukan."
        return FileInputStream(file).use { input ->
            input.bufferedReader().use { it.readText() }
        }
    }

    /**
     * HAPUS BERKAS FILE LOKAL
     */
    fun deleteCertificate(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) file.delete() else false
    }
}

