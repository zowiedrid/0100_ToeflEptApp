require('dotenv').config();
const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const bcrypt = require('bcryptjs');
const { sequelize, User, TestPackage, Question, TestAttempt, Bookmark } = require('./models');
const apiRoutes = require('./routes');

const app = express();
const PORT = process.env.PORT || 3000;

// Middlewares
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Request logging in development
if (process.env.NODE_ENV !== 'production') {
  app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    next();
  });
}

// API Routes
app.use('/api', apiRoutes);

// Root route
app.get('/', (req, res) => {
  res.json({
    name: 'TOEFL EPT Preparation REST API',
    version: '1.0.0',
    developer: 'Ilham Fadhilah (20200140100)',
    endpoints: {
      auth: '/api/auth (login, register, refresh, profile)',
      packages: '/api/packages (CRUDS paket soal)',
      questions: '/api/questions (CRUDS butir soal)',
      attempts: '/api/attempts (CRUDS ujian & riwayat nilai)',
      bookmarks: '/api/bookmarks (CRUDS catatan soal sulit)'
    }
  });
});

async function autoSeedIfEmpty() {
  try {
    const qCount = await Question.count();
    if (qCount > 0) return;

    console.log('🔄 Database kosong. Memulai pengisian (auto-seed) 5 paket soal TOEFL...');
    const seedDataPath = path.resolve(__dirname, 'seeders/toefl_seed_data.json');
    if (!fs.existsSync(seedDataPath)) return;

    // Seed default users
    const hashedMahasiswaPass = await bcrypt.hash('mahasiswa123', 10);
    const hashedDosenPass = await bcrypt.hash('dosen123', 10);

    const [mhs] = await User.findOrCreate({
      where: { email: 'mahasiswa@umy.ac.id' },
      defaults: {
        nama_lengkap: 'Ilham Fadhilah',
        nim: '20200140100',
        email: 'mahasiswa@umy.ac.id',
        password: hashedMahasiswaPass,
        role: 'mahasiswa'
      }
    });

    const [dosen] = await User.findOrCreate({
      where: { email: 'dosen@umy.ac.id' },
      defaults: {
        nama_lengkap: 'Dr. Dosen Pembimbing TOEFL EPT',
        nim: '19850101',
        email: 'dosen@umy.ac.id',
        password: hashedDosenPass,
        role: 'dosen'
      }
    });

    const packages = JSON.parse(fs.readFileSync(seedDataPath, 'utf8'));

    for (const pkg of packages) {
      await TestPackage.findOrCreate({
        where: { id: pkg.id },
        defaults: {
          id: pkg.id,
          title: pkg.title,
          short_description: pkg.shortDescription,
          badge: pkg.badge,
          target_focus: pkg.targetFocus,
          duration_minutes: 115,
          created_by: dosen.id
        }
      });

      const questionRecords = pkg.questions.map(q => {
        const optA = q.options.find(o => o.key === 'a')?.text || '';
        const optB = q.options.find(o => o.key === 'b')?.text || '';
        const optC = q.options.find(o => o.key === 'c')?.text || '';
        const optD = q.options.find(o => o.key === 'd')?.text || '';

        let passageText = null;
        if (q.passageId && pkg.readingPassages) {
          const pass = pkg.readingPassages.find(p => p.id === q.passageId);
          if (pass) passageText = pass.content;
        }

        return {
          id: q.id,
          package_id: pkg.id,
          number: q.number,
          part: q.part,
          type: q.type,
          prompt: q.prompt,
          option_a: optA,
          option_b: optB,
          option_c: optC,
          option_d: optD,
          correct_answer: q.correctAnswer,
          explanation: q.explanation || '',
          audio_script: q.audioScript || null,
          audio_speaker: q.audioSpeaker || null,
          passage_text: passageText
        };
      });

      await Question.bulkCreate(questionRecords, { ignoreDuplicates: true });
    }

    console.log('✓ Auto-seeding selesai. Seluruh butir soal siap digunakan.');
  } catch (err) {
    console.error('✗ Auto-seed error:', err.message);
  }
}

// Database Sync and Server Listen
async function startServer() {
  try {
    await sequelize.authenticate();
    console.log('✓ Database connection established successfully.');

    // Sync database tables without alter: true to preserve SQLite constraints
    await sequelize.sync();
    console.log('✓ Database models synchronized.');

    await autoSeedIfEmpty();

    app.listen(PORT, '0.0.0.0', () => {
      console.log(`🚀 TOEFL EPT REST API Server is running on http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error('✗ Unable to start server:', error);
  }
}

if (require.main === module) {
  startServer();
}

module.exports = app;
