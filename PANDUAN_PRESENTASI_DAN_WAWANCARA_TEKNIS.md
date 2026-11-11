# 🎤 PANDUAN PRESENTASI & KISI-KISI WAWANCARA TEKNIS
## Ujian Akhir Proyek PAM (Pengembangan Aplikasi Mobile) 2026
> **Aplikasi:** TOEFL EPT Prep Mobile Application (`0100_ToeflEptApp`)  
> **Mahasiswa:** Ilham Fadhilah (NIM: 20200140100)  
> **Bobot Komponen Ini:** 30% dari Total Nilai Akhir (7% Demo, 18% Wawancara Teknis, 5% Kesiapan)

---

## BAGIAN 1: RUNDOWN & STRATEGI DEMO APLIKASI (Durasi 15-20 Menit)

### A. Pembukaan & Pengantar (1-2 Menit)
* **Salam & Perkenalan:**  
  *"Selamat pagi/siang Bapak/Ibu Dosen Penguji. Nama saya Ilham Fadhilah, NIM 20200140100. Pada kesempatan kali ini saya mempresentasikan proyek akhir mata kuliah Pengembangan Aplikasi Mobile dengan judul **TOEFL EPT Prep Mobile Application**."*
* **Latar Belakang & Urgensi Masalah:**  
  *"Aplikasi ini saya buat untuk memecahkan masalah nyata mahasiswa semester akhir di kampus kita, di mana salah satu syarat wajib pendaftaran sidang skripsi adalah mencapai skor TOEFL EPT minimal **450**. Aplikasi ini menyediakan simulasi terukur berbasis Android native Jetpack Compose dengan arsitektur MVVM dan backend REST API relasional."*

### B. Alur Demo Fitur (Urutan Ideal untuk Menunjukkan Seluruh 10 Syarat PAM):

1. **Demonstrasi Otentikasi & Otorisasi Peran (Kategori 1 & 2):**
   * Tunjukkan layar **Login**: Masuk menggunakan akun Mahasiswa (`mahasiswa@umy.ac.id` / `mahasiswa123`).
   * Jelaskan bahwa sistem menggunakan **JWT (Access Token 15 menit + Refresh Token 7 hari)** yang disimpan aman di `TokenManager` (SharedPreferences) Android.
   * Perlihatkan perbedaan antarmuka jika login sebagai **Dosen** (`dosen@umy.ac.id`), di mana muncul tombol aksi mengambang (*Floating Action Button*) untuk menambah paket dan butir soal.

2. **Demonstrasi Navigasi & Daftar Paket (Kategori 3, 5, 6, & 10):**
   * Tunjukkan **Home Screen**: Terdapat 5 paket latihan lengkap Part 1 (Listening), Part 2 (Structure), dan Part 3 (Reading).
   * Tunjukkan fitur **Pencarian (Search Paket)**: Ketik *"Fokus"* atau *"Skripsi"*, list akan terfilter secara dinamis.
   * Jelaskan bahwa data ini diambil langsung dari database relasional server via **Retrofit REST API** (`/api/packages`), bukan dari memori statis lokal.

3. **Demonstrasi Sesi Ujian & Timer Per-Part (Kategori 4 & 7):**
   * Klik salah satu paket untuk masuk ke **TestSessionScreen**.
   * Tunjukkan bahwa timer berjalan otomatis secara dinamis sesuai bagian soal:
     * Part 1 (Listening): 35 menit.
     * Part 2 (Structure): 25 menit.
     * Part 3 (Reading): 55 menit.
   * Jawab beberapa soal dengan memilih opsi A, B, C, atau D.
   * Tekan tombol **"Selesai & Kumpulkan"**.

4. **Demonstrasi Hasil Skor & Standar Kelulusan 450:**
   * Tunjukkan layar **ResultSummaryScreen**.
   * Jelaskan bahwa backend secara otomatis menghitung skor mentah ke skor berskala resmi TOEFL ITP (310–677) menggunakan rumus konversi resmi.
   * Tunjukkan indikator kelulusan: Jika $\ge 450$, aplikasi memberi ucapan selamat lulus syarat skripsi; jika $< 450$, menampilkan evaluasi defisit poin.

5. **Demonstrasi Fitur Simpan & Baca File Lokal (Kategori 9 - Sangat Penting!):**
   * Di layar hasil, klik tombol **"Simpan Sertifikat ke File Lokal"**.
   * Muncul toast konfirmasi bahwa berkas laporan teks (`.txt`) berhasil ditulis ke penyimpanan internal HP (`FileOutputStream`).
   * Klik tombol **"Buka Berkas Sertifikat Tersimpan"** (atau buka tab *Sertifikat* di Bottom Navigation).
   * Layar **CertificateScreen** memindai direktori lokal (`FileInputStream`), menampilkan nama file, ukuran dalam KB, dan tanggal pembuatan.
   * Klik tombol ikon mata untuk **membaca isi berkas laporan teks** tersebut secara offline langsung di dalam aplikasi.

6. **Demonstrasi Riwayat & Bookmark (Kategori 6 - CRUDS Penuh):**
   * Buka tab **Riwayat (History)**: Tunjukkan daftar riwayat ujian sebelumnya, statistik skor tertinggi, dan coba hapus salah satu riwayat (*Delete Attempt*).
   * Buka tab **Catatan (Bookmark)**: Tunjukkan kumpulan soal sulit, coba edit catatan refleksi pribadi (*Update Note*), dan cari catatan dengan fitur search.

---

## BAGIAN 2: KISI-KISI PERTANYAAN WAWANCARA TEKNIS (Bobot 18%)

Berikut adalah pertanyaan teknis yang paling sering diajukan dosen penguji beserta jawaban mantap dan tepat:

### Q1: Mengapa aplikasi Anda menggunakan Jetpack Compose, bukan XML Layout tradisional?
> **Jawaban Anda:**  
> *"Jetpack Compose adalah toolkit deklaratif modern dari Google untuk membangun UI Android native secara reaktif. Dibandingkan XML tradisional yang bersifat imperatif, Compose mengeliminasi boilerplate view binding, menyederhanakan sinkronisasi antara UI dan state, serta mengurangi ukuran file APK secara signifikan karena seluruh UI didefinisikan murni menggunakan fungsi Kotlin (`@Composable`)."*

### Q2: Bagaimana arsitektur MVVM diterapkan di aplikasi Anda? Di mana logika bisnis dan di mana tampilan?
> **Jawaban Anda:**  
> *"Saya memisahkan kode menjadi 3 lapisan utama:*
> 1. *Lapisan **Model**: Data class seperti `User`, `TestPackage`, `Question`, dan `TestAttempt` yang memetakan response JSON dari server.*
> 2. *Lapisan **Repository**: `TestRepository`, `AuthRepository`, dll., yang bertanggung jawab melakukan komunikasi data ke REST API melalui Retrofit secara asinkron menggunakan Kotlin Coroutine.*
> 3. *Lapisan **ViewModel**: Seperti `TestViewModel` dan `HomeViewModel`, bertugas menampung state menggunakan `StateFlow<UiState>`. ViewModel bertahan saat terjadi konfigurasi ulang (seperti rotasi layar).*
> 4. *Lapisan **View (Compose)**: Composable function hanya mengamati (`collectAsState()`) data dari ViewModel dan merender UI tanpa menyimpan logika bisnis aplikasi."*

### Q3: Kenapa di pedoman Room dilarang, dan database apa yang Anda gunakan?
> **Jawaban Anda:**  
> *"Dosen melarang penggunaan Room lokal karena mata kuliah PAM menguji kemampuan mahasiswa dalam arsitektur Client-Server berbasis REST API jaringan. Oleh karena itu, saya menggunakan database relasional server (**MySQL/PostgreSQL**) yang dimodelkan menggunakan **Sequelize ORM** pada backend Node.js/Express. Android berkomunikasi via protokol HTTP menggunakan library Retrofit 2 dan OkHttp."*

### Q4: Bagaimana cara kerja Authentication JWT (Access Token & Refresh Token) di aplikasi Anda?
> **Jawaban Anda:**  
> *"Saat pengguna login, backend mengembalikan dua token:*
> 1. *`accessToken`: Memiliki masa aktif singkat (15 menit). Di Android, token ini disisipkan secara otomatis pada setiap request HTTP menggunakan `AuthInterceptor` pada OkHttp.*
> 2. *`refreshToken`: Memiliki masa aktif panjang (7 hari). Jika `accessToken` kedaluwarsa (HTTP 401), aplikasi dapat memanggil endpoint `/api/auth/refresh` untuk menerbitkan `accessToken` baru tanpa memaksa pengguna mengetik kata sandi kembali.*
> *Pasangan token ini disimpan pada SharedPreferences perangkat melalui kelas `TokenManager`."*

### Q5: Bagaimana implementasi Kategori 9 (Simpan dan Baca File Lokal)? Di mana file disimpan?
> **Jawaban Anda:**  
> *"Implementasi ini ada pada kelas `CertificateFileManager.kt`:*
> * *Untuk **Simpan File**: Saya menggunakan `FileOutputStream` untuk menulis data laporan hasil skor TOEFL EPT ke dalam direktori aplikasi internal (`context.filesDir/toefl_certificates/`).*
> * *Untuk **Baca File**: Saya memindai direktori tersebut menggunakan `File.listFiles()`, menghitung ukuran berkasnya, dan membaca isi teks dokumennya menggunakan `FileInputStream` dan `BufferedReader` untuk ditampilkan pada dialog Compose.*
> * *Pendekatan penyimpanan internal ini aman karena tidak memerlukan permission runtime berbahaya di Android versi baru dan berkas tetap terlindungi dalam sandbox aplikasi."*

### Q6: Bagaimana Anda menerapkan Role-Based Authorization?
> **Jawaban Anda:**  
> *"Pada tabel `users`, terdapat atribut `role` dengan nilai `'mahasiswa'` atau `'dosen'`. Pada sisi backend, terdapat middleware `requireRole('dosen')` untuk melindungi endpoint pembuatan paket dan pengeditan butir soal. Pada sisi Android (Jetpack Compose), tampilan beradaptasi secara dinamis: jika `currentUser.role == 'dosen'`, maka tombol FAB 'Tambah Paket' dan menu manajemen bank soal akan ditampilkan."*

---

## BAGIAN 3: TIPS KESIAPAN DAN SIKAP PROFESIONAL (Bobot 5%)

1. **Jalankan Backend Terlebih Dahulu Sebelum Dipanggil Masuk:**
   * Di terminal laptop, buka folder backend dan jalankan `npm run dev` sehingga server API aktif di `http://localhost:3000`.
2. **Buka Project di Android Studio & Siapkan Emulator/HP:**
   * Pastikan emulator Android atau HP fisik sudah menampilkan halaman login `0100_ToeflEptApp` sehingga begitu giliran presentasi dimulai, aplikasi sudah langsung *live* di layar.
3. **Buka Tab GitHub di Browser:**
   * Siapkan tab browser yang membuka repositori GitHub Anda:  
     `https://github.com/zowiedrid/0100_ToeflEptApp`  
   * Tunjukkan bahwa repositori memiliki **36 commit bertahap** dan dokumentasi README yang sangat lengkap.
4. **Bicara Santai, Terstruktur, dan Percaya Diri:**
   * Gunakan istilah teknis yang tepat: *Compose, ViewModel, StateFlow, Retrofit, JWT Bearer Token, FileOutputStream*.

