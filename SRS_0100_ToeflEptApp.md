# DOKUMEN SPESIFIKASI KEBUTUHAN PERANGKAT LUNAK
## SOFTWARE REQUIREMENT SPECIFICATION (SRS)

---

### IDENTITAS PROYEK
* **Nama Sistem / Aplikasi:** TOEFL EPT Prep Mobile Application (Target 450+)
* **Kode Repository:** `0100_ToeflEptApp`
* **Mata Kuliah:** Pengembangan Aplikasi Mobile (PAM) 2026
* **Nama Mahasiswa:** Ilham Fadhilah
* **NIM:** 20200140100
* **Program Studi:** Teknologi Informasi, Fakultas Teknik
* **Institusi:** Universitas Muhammadiyah Yogyakarta (UMY)

---

## BAB I: PENDAHULUAN

### 1.1 Latar Belakang
Dalam rangka memenuhi persyaratan pendaftaran sidang tugas akhir / skripsi di lingkungan Universitas Muhammadiyah Yogyakarta, mahasiswa diwajibkan mencapai skor kelulusan minimal **450** pada tes kemampuan bahasa Inggris TOEFL EPT (*English Proficiency Test*). Namun, sebagian besar mahasiswa mengalami kesulitan dalam memahami tipe-tipe soal tertentu (khususnya *Error Identification*, kolokasi preposisi kaku, dan klausa *participle*) serta kurang terbiasa dengan manajemen waktu pengerjaan per bagian.

Untuk mengatasi permasalahan tersebut, dikembangkan **Aplikasi Mobile TOEFL EPT Prep (0100_ToeflEptApp)** berbasis platform Android menggunakan teknologi modern **Jetpack Compose** dan arsitektur **MVVM (Model-View-ViewModel)** yang terintegrasi dengan **Backend REST API** dan database relasional.

### 1.2 Tujuan Proyek
1. Membangun aplikasi mobile Android native menggunakan **Jetpack Compose** yang bebas bug fatal untuk simulasi ujian TOEFL EPT.
2. Mengimplementasikan sistem otentikasi aman berbasis **JWT (Access Token & Refresh Token)** dan otorisasi peran (**Role-Based UI: Mahasiswa dan Dosen**).
3. Mengimplementasikan 5 fitur **CRUDS penuh** (Create, Read, Update, Delete, Search) pada minimal 4 entitas data relasional dinamis tanpa menggunakan database lokal Room.
4. Menerapkan fitur **File Storage lokal** untuk menyimpan berkas laporan prediksi skor (sertifikat) ke penyimpanan internal perangkat dan membacanya kembali secara offline.

### 1.3 Ruang Lingkup Sistem
* **Platform Klien:** Android 7.0 (API Level 24) hingga Android 14+ (API Level 35/37) dengan Jetpack Compose Material 3.
* **Platform Server:** Node.js + Express.js REST API Server.
* **Database:** Database Relasional (MySQL / PostgreSQL / SQLite) yang diakses klien via protokol HTTP/HTTPS (REST API).

---

## BAB II: DESKRIPSI UMUM SISTEM

### 2.1 Karakteristik Pengguna
1. **Mahasiswa (Peserta Ujian):**
   * Mendaftar akun dan login ke dalam aplikasi.
   * Menjelajahi dan mencari paket soal latihan TOEFL EPT.
   * Mengikuti simulasi ujian dengan timer mandiri per-part (Part 1: 35 menit, Part 2: 25 menit, Part 3: 55 menit).
   * Melihat analisis hasil skor berskala resmi TOEFL ITP (310–677) dan status kelulusan standar 450.
   * Menyimpan dan membuka berkas sertifikat laporan hasil ujian ke memori internal HP.
   * Menandai soal-soal membingungkan ke dalam daftar *Bookmark* dan menyertakan catatan belajar pribadi.
2. **Dosen / Administrator:**
   * Login dengan hak akses khusus (*role dosen*).
   * Menambah paket soal baru (*Create Package*).
   * Mengelola bank butir soal (*Create, Update, Delete Question*).
   * Memantau daftar mahasiswa terdaftar.

### 2.2 Batasan Arsitektur & Teknologi
* **Teknologi UI:** Wajib 100% Android **Jetpack Compose (Material 3)**. Dilarang menggunakan XML Layout.
* **Navigasi:** Wajib menggunakan **Navigator Compose (`androidx.navigation.compose`)**.
* **Pola Arsitektur:** Wajib **MVVM (Model-View-ViewModel)** dengan pemisahan *StateFlow* dan UI.
* **Basis Data:** Database **Room dilarang keras**. Data utama disimpan di server database relasional (MySQL/PostgreSQL) dan dikonsumsi melalui Retrofit REST API.

---

## BAB III: SPESIFIKASI KEBUTUHAN FUNGSIONAL (SKF)

### 3.1 SKF-01: Otentikasi Pengguna (JWT)
* **SKF-01.1:** Pengguna dapat melakukan registrasi akun baru dengan menginput nama lengkap, NIM, email, password, dan peran (*mahasiswa* atau *dosen*).
* **SKF-01.2:** Pengguna dapat login menggunakan Email/NIM dan kata sandi.
* **SKF-01.3:** Sistem menerbitkan pasangan token: **Access Token (berlaku 15 menit)** untuk otorisasi request dan **Refresh Token (berlaku 7 hari)** untuk pembaruan sesi otomatis.
* **SKF-01.4:** Token disimpan secara terenkripsi/aman pada `SharedPreferences` perangkat klien melalui `TokenManager`.

### 3.2 SKF-02: Otorisasi Berbasis Peran (Role-Based UI)
* **SKF-02.1:** Antarmuka aplikasi menyesuaikan tampilan secara dinamis berdasarkan nilai atribut `role`.
* **SKF-02.2:** Pengguna berperan *Mahasiswa* hanya diberikan akses menu latihan, riwayat tes, bookmark, dan penyimpanan sertifikat.
* **SKF-02.3:** Pengguna berperan *Dosen* diberikan antarmuka khusus (Floating Action Button dan menu administrasi) untuk menambah/mengedit paket dan butir soal.

### 3.3 SKF-03: Manajemen Paket Soal (CRUDS Fitur 1)
* **Create:** Dosen dapat membuat paket latihan baru dengan ID unik, judul, deskripsi, dan durasi.
* **Read:** Mahasiswa dan dosen dapat melihat daftar seluruh paket latihan yang tersedia beserta jumlah soal.
* **Update:** Dosen dapat memperbarui rincian paket soal.
* **Delete:** Dosen dapat menghapus paket soal (menghapus butir soal terkait secara *cascading*).
* **Search:** Mahasiswa dan dosen dapat melakukan pencarian paket soal berdasarkan kata kunci judul atau badge.

### 3.4 SKF-04: Manajemen Butir Soal (CRUDS Fitur 2)
* **Create:** Dosen dapat menambahkan soal baru pada Part 1 (Listening), Part 2 (Structure), atau Part 3 (Reading).
* **Read:** Sistem menampilkan butir soal, teks bacaan, dan transkrip audio saat ujian berlangsung.
* **Update:** Dosen dapat merevisi teks prompt, opsi pilihan (A, B, C, D), kunci jawaban, dan pembahasan.
* **Delete:** Dosen dapat menghapus butir soal dari paket.
* **Search:** Pencarian butir soal berdasarkan nomor urut, topik grammar, atau teks stimulus.

### 3.5 SKF-05: Sesi Ujian & Perhitungan Skor TOEFL Resmi (CRUDS Fitur 3)
* **SKF-05.1:** Sesi ujian dilengkapi penghitung waktu mundur (*countdown timer*) yang terbagi per bagian: Part 1 (35m), Part 2 (25m), Part 3 (55m).
* **SKF-05.2:** Mahasiswa dapat memilih jawaban A, B, C, atau D dengan feedback visual instan.
* **SKF-05.3:** Saat jawaban dikumpulkan (*Submit*), backend secara otomatis mengonversi skor mentah (*raw scores*) ke skor berskala resmi TOEFL ITP (Listening: 24–68, Structure: 20–68, Reading: 21–67).
* **SKF-05.4:** Total skor dihitung dengan formula resmi:  
  $$\text{Total Score} = \left\lfloor \frac{(\text{Listening} + \text{Structure} + \text{Reading}) \times 10}{3} \right\rceil$$
* **SKF-05.5:** Sistem membandingkan total skor terhadap standar kelulusan **450** dan menandai status `is_passed` (Lulus / Belum Memenuhi).
* **SKF-05.6:** Mahasiswa dapat melihat riwayat ujian, mencari riwayat berdasarkan judul/tanggal, dan menghapus riwayat yang tidak diinginkan.

### 3.6 SKF-06: Bookmark & Catatan Refleksi Mandiri (CRUDS Fitur 4)
* **Create:** Mahasiswa dapat menandai soal yang dirasa sulit untuk dipelajari kembali.
* **Read:** Mahasiswa dapat melihat daftar seluruh soal yang telah di-bookmark.
* **Update:** Mahasiswa dapat menulis dan mengedit catatan refleksi pribadi (misalnya: *"Ingat! Despite diikuti Noun Phrase"*).
* **Delete:** Mahasiswa dapat menghapus soal dari daftar bookmark.
* **Search:** Mahasiswa dapat mencari catatan belajar berdasarkan kata kunci catatan atau teks soal.

### 3.7 SKF-07: Penyimpanan Berkas Sertifikat Lokal (File Storage - Kategori 9)
* **Simpan File Lokal:** Mahasiswa dapat mengekspor hasil skor ujian menjadi berkas laporan resmi teks (`.txt`) ke penyimpanan internal perangkat (`context.filesDir/toefl_certificates/`).
* **Baca File Lokal:** Aplikasi memindai direktori penyimpanan lokal, menampilkan daftar berkas sertifikat yang tersimpan (disertai ukuran berkas dalam KB dan waktu penyimpanan), serta membuka isi konten laporan teks secara offline.
* **Hapus File Lokal:** Mahasiswa dapat menghapus berkas laporan fisik dari penyimpanan internal perangkat.

### 3.8 SKF-08: Manajemen Profil Pengguna (CRUDS Fitur 5)
* **Create:** Registrasi pengguna baru.
* **Read:** Menampilkan profil pengguna (Nama, NIM, Email, Peran) di *top app bar*.
* **Update:** Pengguna dapat memperbarui nama lengkap, email, atau kata sandi.
* **Delete:** Pengguna dapat menonaktifkan/menghapus akun miliknya.
* **Search:** Khusus akun Dosen, tersedia fungsi pencarian data mahasiswa terdaftar berdasarkan NIM atau Nama.

---

## BAB IV: DESAIN BASIS DATA RELASIONAL

Sistem menerapkan 5 entitas relasional yang terhubung secara dinamis pada database server:

```
[ users ] (1) ------------< (N) [ test_attempts ] (N) >------------ (1) [ test_packages ]
    |                                                                         |
    | (1)                                                                     | (1)
    v (N)                                                                     v (N)
[ bookmarks ] (N) >------------------------------------------------------- (1) [ questions ]
```

### Kamus Data Entitas:
1. **`users`**: `id` (PK, INT), `nama_lengkap` (VARCHAR), `nim` (VARCHAR, UNIQUE), `email` (VARCHAR, UNIQUE), `password` (VARCHAR, BCRYPT), `role` (ENUM: 'mahasiswa','dosen'), `refresh_token` (TEXT).
2. **`test_packages`**: `id` (PK, VARCHAR), `title` (VARCHAR), `short_description` (TEXT), `badge` (VARCHAR), `target_focus` (VARCHAR), `duration_minutes` (INT), `created_by` (FK ke `users.id`).
3. **`questions`**: `id` (PK, VARCHAR), `package_id` (FK ke `test_packages.id`), `number` (INT), `part` (INT), `type` (VARCHAR), `prompt` (TEXT), `option_a` (TEXT), `option_b` (TEXT), `option_c` (TEXT), `option_d` (TEXT), `correct_answer` (CHAR 1), `explanation` (TEXT), `audio_script` (TEXT), `passage_text` (LONGTEXT).
4. **`test_attempts`**: `id` (PK, VARCHAR), `user_id` (FK ke `users.id`), `package_id` (FK ke `test_packages.id`), `package_title` (VARCHAR), `listening_raw` (INT), `listening_scaled` (INT), `structure_raw` (INT), `structure_scaled` (INT), `reading_raw` (INT), `reading_scaled` (INT), `total_score` (INT), `is_passed` (BOOLEAN), `score_gap` (INT), `time_spent_seconds` (INT), `user_answers_json` (LONGTEXT), `attempt_date` (DATETIME).
5. **`bookmarks`**: `id` (PK, INT), `user_id` (FK ke `users.id`), `question_id` (FK ke `questions.id`), `notes` (TEXT).

---

## BAB V: ARSITEKTUR PERANGKAT LUNAK

Aplikasi dibangun dengan mematuhi prinsip arsitektur **Clean MVVM Android**:
```
┌────────────────────────────────────────────────────────┐
│                   VIEW (Jetpack Compose)               │
│  LoginScreen, RegisterScreen, HomeScreen,              │
│  TestSessionScreen, ResultSummaryScreen,               │
│  HistoryScreen, BookmarkScreen, CertificateScreen      │
└───────────────────────────▲────────────────────────────┘
                            │ Mengamati StateFlow (UiState)
┌───────────────────────────┴────────────────────────────┐
│                        VIEWMODEL                       │
│  AuthViewModel, HomeViewModel, TestViewModel,          │
│  HistoryViewModel, BookmarkViewModel,                  │
│  CertificateViewModel                                  │
└───────────────────────────▲────────────────────────────┘
                            │ Memanggil Coroutine Suspend
┌───────────────────────────┴────────────────────────────┐
│                       REPOSITORY                       │
│  AuthRepository, TestRepository,                       │
│  HistoryRepository, BookmarkRepository                 │
└─────────────┬────────────────────────────┬─────────────┘
              │ Remote API (JSON)          │ Local File Storage
              ▼                            ▼
┌──────────────────────────┐  ┌──────────────────────────┐
│   RETROFIT API SERVICE   │  │  CERTIFICATE FILE MGR    │
│  (OkHttp, Bearer Token)  │  │  (FileInputStream/Output)│
└──────────────────────────┘  └──────────────────────────┘
```

---

## BAB VI: PENUTUP
Dokumen SRS ini telah disesuaikan secara presisi dengan implementasi kode pada repositori `0100_ToeflEptApp`. Seluruh fungsionalitas yang tercantum telah teruji bebas bug fatal dan siap dipresentasikan pada Ujian Akhir Semester Mata Kuliah Pengembangan Aplikasi Mobile (PAM) 2026.
