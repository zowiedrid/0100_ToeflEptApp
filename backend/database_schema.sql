-- ==========================================================
-- SKEMA DATABASE RELASIONAL: TOEFL EPT PREPARATION
-- Project Akhir PAM (Pengembangan Aplikasi Mobile) 2026
-- Mahasiswa: Ilham Fadhilah (NIM: 20200140100)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `toefl_ept_db`;
USE `toefl_ept_db`;

-- 1. TABEL USERS (Entitas 1)
-- Mendukung Authentication JWT & Role-based Authorization ('mahasiswa' & 'dosen')
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama_lengkap` VARCHAR(255) NOT NULL,
  `nim` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` ENUM('mahasiswa', 'dosen') DEFAULT 'mahasiswa' NOT NULL,
  `refresh_token` TEXT NULL,
  `createdAt` DATETIME NOT NULL,
  `updatedAt` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TABEL TEST_PACKAGES (Entitas 2)
-- Menyimpan paket-paket latihan soal TOEFL EPT
CREATE TABLE IF NOT EXISTS `test_packages` (
  `id` VARCHAR(100) PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `short_description` TEXT NOT NULL,
  `badge` VARCHAR(100) DEFAULT 'Soal Latihan EPT',
  `target_focus` VARCHAR(255) NULL,
  `duration_minutes` INT DEFAULT 115,
  `created_by` INT NULL,
  `createdAt` DATETIME NOT NULL,
  `updatedAt` DATETIME NOT NULL,
  FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. TABEL QUESTIONS (Entitas 3)
-- Butir soal 3 Part (Listening, Structure, Reading)
CREATE TABLE IF NOT EXISTS `questions` (
  `id` VARCHAR(100) PRIMARY KEY,
  `package_id` VARCHAR(100) NOT NULL,
  `number` INT NOT NULL,
  `part` INT NOT NULL COMMENT '1=Listening, 2=Structure, 3=Reading',
  `type` VARCHAR(50) NOT NULL,
  `prompt` TEXT NOT NULL,
  `option_a` TEXT NOT NULL,
  `option_b` TEXT NOT NULL,
  `option_c` TEXT NOT NULL,
  `option_d` TEXT NOT NULL,
  `correct_answer` CHAR(1) NOT NULL,
  `explanation` TEXT NULL,
  `audio_script` TEXT NULL,
  `audio_speaker` VARCHAR(100) NULL,
  `passage_text` LONGTEXT NULL,
  `createdAt` DATETIME NOT NULL,
  `updatedAt` DATETIME NOT NULL,
  FOREIGN KEY (`package_id`) REFERENCES `test_packages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. TABEL TEST_ATTEMPTS (Entitas 4)
-- Riwayat & Sesi Pengerjaan Tes TOEFL Mahasiswa dengan Skala Resmi 310 - 677
CREATE TABLE IF NOT EXISTS `test_attempts` (
  `id` VARCHAR(100) PRIMARY KEY,
  `user_id` INT NOT NULL,
  `package_id` VARCHAR(100) NOT NULL,
  `package_title` VARCHAR(255) NOT NULL,
  `listening_raw` INT DEFAULT 0,
  `listening_total` INT DEFAULT 0,
  `listening_scaled` INT DEFAULT 24,
  `structure_raw` INT DEFAULT 0,
  `structure_total` INT DEFAULT 0,
  `structure_scaled` INT DEFAULT 20,
  `reading_raw` INT DEFAULT 0,
  `reading_total` INT DEFAULT 0,
  `reading_scaled` INT DEFAULT 21,
  `total_score` INT DEFAULT 310,
  `is_passed` TINYINT(1) DEFAULT 0 COMMENT '1 jika total_score >= 450',
  `score_gap` INT DEFAULT -140,
  `time_spent_seconds` INT DEFAULT 0,
  `user_answers_json` LONGTEXT NULL,
  `attempt_date` DATETIME NOT NULL,
  `createdAt` DATETIME NOT NULL,
  `updatedAt` DATETIME NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`package_id`) REFERENCES `test_packages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TABEL BOOKMARKS (Entitas Relasi Tambahan untuk CRUDS Catatan Pribadi)
CREATE TABLE IF NOT EXISTS `bookmarks` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `question_id` VARCHAR(100) NOT NULL,
  `notes` TEXT NULL,
  `createdAt` DATETIME NOT NULL,
  `updatedAt` DATETIME NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

