require('dotenv').config();
const bcrypt = require('bcryptjs');
const fs = require('fs');
const path = require('path');
const { sequelize, User, TestPackage, Question, TestAttempt, Bookmark } = require('../models');

async function seed() {
  try {
    console.log('🔄 Connecting to database and synchronizing tables...');
    await sequelize.sync({ force: true });
    console.log('✓ Tables created/reset successfully.');

    // 1. Seed Users
    console.log('👤 Seeding default users...');
    const hashedMahasiswaPass = await bcrypt.hash('mahasiswa123', 10);
    const hashedDosenPass = await bcrypt.hash('dosen123', 10);

    const mhs = await User.create({
      nama_lengkap: 'Ilham Fadhilah',
      nim: '20200140100',
      email: 'mahasiswa@umy.ac.id',
      password: hashedMahasiswaPass,
      role: 'mahasiswa'
    });

    const dosen = await User.create({
      nama_lengkap: 'Dr. Dosen Pembimbing TOEFL EPT',
      nim: '19850101',
      email: 'dosen@umy.ac.id',
      password: hashedDosenPass,
      role: 'dosen'
    });
    console.log('✓ Users created: mahasiswa@umy.ac.id & dosen@umy.ac.id');

    // 2. Seed Packages and Questions
    console.log('📚 Seeding TOEFL Packages & Questions...');
    const seedDataPath = path.resolve(__dirname, 'toefl_seed_data.json');
    if (!fs.existsSync(seedDataPath)) {
      console.error('✗ toefl_seed_data.json not found!');
      return;
    }

    const packages = JSON.parse(fs.readFileSync(seedDataPath, 'utf8'));

    for (const pkg of packages) {
      await TestPackage.create({
        id: pkg.id,
        title: pkg.title,
        short_description: pkg.shortDescription,
        badge: pkg.badge,
        target_focus: pkg.targetFocus,
        duration_minutes: 115,
        created_by: dosen.id
      });

      const questionRecords = pkg.questions.map(q => {
        const optA = q.options.find(o => o.key === 'a')?.text || '';
        const optB = q.options.find(o => o.key === 'b')?.text || '';
        const optC = q.options.find(o => o.key === 'c')?.text || '';
        const optD = q.options.find(o => o.key === 'd')?.text || '';

        // find passage text if applicable
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

      await Question.bulkCreate(questionRecords);
      console.log(`  ✓ Seeded ${pkg.title} with ${questionRecords.length} questions`);
    }

    // 3. Seed 2 Initial Test Attempts for Mahasiswa
    console.log('📊 Seeding initial test attempts for student...');
    await TestAttempt.create({
      id: 'attempt-demo-1',
      user_id: mhs.id,
      package_id: 'test-1',
      package_title: 'Paket 1: Soal Resmi EPT Kampus 2020',
      listening_raw: 20,
      listening_total: 38,
      listening_scaled: 46,
      structure_raw: 15,
      structure_total: 40,
      structure_scaled: 40,
      reading_raw: 20,
      reading_total: 42,
      reading_scaled: 43,
      total_score: 430,
      is_passed: false,
      score_gap: -20,
      time_spent_seconds: 3250,
      user_answers_json: JSON.stringify({ 't1-p2-q16': 'b', 't1-p2-q18': 'c' }),
      attempt_date: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000) // 3 days ago
    });

    await TestAttempt.create({
      id: 'attempt-demo-2',
      user_id: mhs.id,
      package_id: 'test-2',
      package_title: 'Paket 2: Fokus Structure & Pola Kunci 450',
      listening_raw: 27,
      listening_total: 40,
      listening_scaled: 52,
      structure_raw: 26,
      structure_total: 40,
      structure_scaled: 50,
      reading_raw: 26,
      reading_total: 40,
      reading_scaled: 48,
      total_score: 500,
      is_passed: true,
      score_gap: 50,
      time_spent_seconds: 2900,
      user_answers_json: JSON.stringify({ 't2-p2-q1': 'b', 't2-p2-q2': 'a' }),
      attempt_date: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000) // 1 day ago
    });

    // 4. Seed 1 Sample Bookmark
    console.log('📌 Seeding sample bookmark...');
    await Bookmark.create({
      user_id: mhs.id,
      question_id: 't1-p2-q16',
      notes: 'Pola Error Identification: Despite harus diikuti Frasa Benda, bukan klausa subjek+verb!'
    });

    console.log('\n🎉 ALL SEEDING COMPLETED SUCCESSFULLY!');
    console.log('Mahasiswa Login: email="mahasiswa@umy.ac.id", password="mahasiswa123"');
    console.log('Dosen Login:     email="dosen@umy.ac.id", password="dosen123"');
    process.exit(0);
  } catch (error) {
    console.error('✗ Seeding error:', error);
    process.exit(1);
  }
}

seed();

