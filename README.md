# 🎓 0100_ToeflEptApp - Aplikasi TOEFL EPT Prep Mobile (Target 450+)
> **Project Akhir Mata Kuliah Pengembangan Aplikasi Mobile (PAM) 2026**  
> **Nama Mahasiswa:** Ilham Fadhilah  
> **NIM:** 20200140100 (4 Digit Belakang: `0100`)  
> **Program Studi:** Teknologi Informasi, Universitas Muhammadiyah Yogyakarta  

---

## 📌 Deskripsi Proyek
Aplikasi mobile native berbasis **Android Jetpack Compose** yang dirancang untuk membantu mahasiswa mempersiapkan ujian **TOEFL EPT (English Proficiency Test)** sebagai persyaratan kelulusan pendaftaran sidang tugas akhir / skripsi dengan batas nilai kelulusan minimal **450**.

Aplikasi ini terhubung langsung ke **Backend REST API** dengan database relasional (MySQL / PostgreSQL / SQLite), mendukung otentikasi aman berbasis **JWT (Access Token & Refresh Token)**, **Role-Based UI** (Mahasiswa vs Dosen), **Arsitektur MVVM**, serta fitur **Simpan & Baca File Lokal (File Storage)** untuk mengekspor sertifikat hasil ujian.

---

## 📋 Pemenuhan 10 Persyaratan Minimal Fitur PAM (Sesuai Pedoman)

| No | Kategori | Ketentuan Minimal | Implementasi pada Aplikasi Ini |
| :---: | :--- | :--- | :--- |
| **1** | **Authentication** | Login, Register, JWT Access Token & Refresh Token | Menggunakan JWT Access Token (15 menit) + Refresh Token (7 hari) yang disimpan aman di `TokenManager` (`SharedPreferences`). |
| **2** | **Authorization** | Role-based UI (Minimal 1 level peran) | **Role Mahasiswa:** Mengikuti ujian, bookmark soal, simpan sertifikat.<br>**Role Dosen:** Memiliki FAB untuk menambah paket soal baru dan mengelola bank soal. |
| **3** | **Navigasi & Routing** | Multi-page app hanya menggunakan Navigator Compose | Menggunakan `androidx.navigation.compose` (`Navigasi.kt`) dengan `NavHost`, `NavController`, dan sealed class `Screen`. |
| **4** | **State Management** | Wajib menggunakan MVVM | Memisahkan View dan Model menggunakan `AuthViewModel`, `HomeViewModel`, `TestViewModel`, `HistoryViewModel`, `BookmarkViewModel`, dan `CertificateViewModel` via `StateFlow`. |
| **5** | **Tabel / Data** | Minimal 4 Entitas Data Utama yang saling terhubung dinamis | 1. `users` (id, nama, nim, email, password, role, refresh_token)<br>2. `test_packages` (id, title, desc, badge, duration)<br>3. `questions` (id, package_id, part, type, prompt, options, answer)<br>4. `test_attempts` (id, user_id, package_id, scaled_scores, total_score, is_passed)<br>5. `bookmarks` (id, user_id, question_id, notes) |
| **6** | **Features (CRUDS)** | Minimal 5 fitur dengan method CRUDS penuh | 1. **CRUDS Paket Soal:** Tambah, lihat, ubah, hapus, cari paket.<br>2. **CRUDS Butir Soal:** Pengelolaan butir soal 3 Part.<br>3. **CRUDS Sesi Ujian & Riwayat:** Submit jawaban, hitung skor, hapus riwayat, cari riwayat.<br>4. **CRUDS Bookmark Catatan:** Simpan soal sulit, edit catatan refleksi, hapus, cari.<br>5. **CRUDS Profil Pengguna:** Register, update nama/password, hapus akun, cari mahasiswa. |
| **7** | **REST API Integration** | Request & parsing JSON dari REST API | Menggunakan **Retrofit 2** + **Gson Converter** + **OkHttp Logging Interceptor**. |
| **8** | **Database** | Minimal MySQL / Postgres (Room dilarang) | Database server-side relasional menggunakan **Sequelize ORM** yang mendukung MySQL, PostgreSQL, dan SQLite. |
| **9** | **File Storage** | Simpan dan baca file lokal | **Simpan File:** Fitur ekspor sertifikat prediksi skor TOEFL resmi ke internal storage HP (`FileOutputStream`).<br>**Baca File:** Membaca daftar berkas sertifikat dan membuka isi teks laporan secara offline (`FileInputStream`). |
| **10** | **UI/UX** | Penerapan hanya Jetpack Compose | 100% Declarative UI menggunakan Jetpack Compose Material 3. |

---

## 🗄️ Skema Database Relasional

Skema DDL lengkap tersedia pada berkas: `backend/database_schema.sql`

```
[ users ] (1) ------------< (N) [ test_attempts ] (N) >------------ (1) [ test_packages ]
    |                                                                         |
    | (1)                                                                     | (1)
    v (N)                                                                     v (N)
[ bookmarks ] (N) >------------------------------------------------------- (1) [ questions ]
```

---

## 🚀 Panduan Menjalankan Aplikasi

### 1. Menjalankan Backend REST API
```bash
cd backend

# Install dependensi
npm install

# Jalankan seeder database (membuat tabel & memasukkan 5 paket soal TOEFL + akun demo)
npm run seed

# Jalankan server API (port 3000)
npm run dev
```

> **Akun Demo Bawaan:**
> * **Mahasiswa:** `mahasiswa@umy.ac.id` / `mahasiswa123` (NIM: `20200140100`)
> * **Dosen:** `dosen@umy.ac.id` / `dosen123` (NIM: `19850101`)

### 2. Menjalankan Aplikasi Android di Android Studio
1. Buka folder `0100_ToeflEptApp` menggunakan **Android Studio**.
2. Biarkan Gradle melakukan sync dependensi otomatis.
3. Jalankan emulator Android atau hubungkan HP fisik dengan USB Debugging aktif.
4. Klik tombol **Run 'app'** (`Shift + F10`).
5. APK siap pakai juga tersedia di: `app/build/outputs/apk/debug/app-debug.apk`.

*Catatan Jaringan Android:*
* Jika menggunakan **Android Emulator**, API URL otomatis mengarah ke `http://10.0.2.2:3000/`.
* Jika menggunakan **HP Fisik**, sesuaikan IP di `NetworkClient.kt` dengan IP WiFi laptop Anda (misal: `http://192.168.1.X:3000/`).

---

## 📈 Log Progres Mingguan (Weekly Milestones)
* **Minggu 1:** Analisis kebutuhan sistem, penyusunan SRS, perancangan skema ERD 4 entitas utama.
* **Minggu 2:** Inisialisasi arsitektur REST API Express.js, integrasi Sequelize database, dan migrasi tabel.
* **Minggu 3:** Implementasi Authentication JWT (Access Token 15m & Refresh Token 7d) serta Role Middleware.
* **Minggu 4:** Pengisian (seeder) 5 paket soal TOEFL resmi Part 1, 2, 3 dan implementasi CRUDS Paket & Soal.
* **Minggu 5:** Setup project Android Jetpack Compose, konfigurasi Gradle version catalog, Retrofit, dan theme.
* **Minggu 6:** Implementasi Auth Screen (Login & Register) terhubung API serta penyimpanan token JWT.
* **Minggu 7:** Pembuatan Home Screen, Dashboard Target 450, dan fitur pencarian paket latihan.
* **Minggu 8:** Implementasi Sesi Ujian (TestSessionScreen) dengan timer per-part dan seleksi jawaban dinamis.
* **Minggu 9:** Implementasi kalkulasi skor resmi TOEFL (310–677) dan tampilan Result Summary kelulusan 450.
* **Minggu 10:** Implementasi History Screen & Bookmark Catatan Soal Sulit (CRUDS penuh).
* **Minggu 11:** Implementasi File Storage lokal (Simpan & Baca File Sertifikat Prediksi Skor TOEFL EPT).
* **Minggu 12:** Pengujian menyeluruh, build APK debug, dan finalisasi dokumentasi proyek.
