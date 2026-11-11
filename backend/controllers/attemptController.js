const { Op } = require('sequelize');
const { TestAttempt, TestPackage, Question } = require('../models');

// Conversion tables
const LISTENING_TABLE = {
  0: 24, 1: 25, 2: 26, 3: 27, 4: 28, 5: 29, 6: 30, 7: 31, 8: 32, 9: 32,
  10: 33, 11: 35, 12: 37, 13: 38, 14: 39, 15: 41, 16: 41, 17: 42, 18: 43, 19: 44,
  20: 45, 21: 45, 22: 46, 23: 47, 24: 47, 25: 48, 26: 48, 27: 49, 28: 49, 29: 50,
  30: 51, 31: 51, 32: 52, 33: 52, 34: 53, 35: 54, 36: 54, 37: 55, 38: 56, 39: 57,
  40: 57, 41: 58, 42: 59, 43: 60, 44: 61, 45: 62, 46: 63, 47: 65, 48: 66, 49: 67, 50: 68
};

const STRUCTURE_TABLE = {
  0: 20, 1: 20, 2: 21, 3: 22, 4: 23, 5: 25, 6: 26, 7: 27, 8: 29, 9: 31,
  10: 33, 11: 35, 12: 36, 13: 37, 14: 38, 15: 40, 16: 40, 17: 41, 18: 42, 19: 43,
  20: 44, 21: 45, 22: 46, 23: 47, 24: 48, 25: 49, 26: 50, 27: 51, 28: 52, 29: 53,
  30: 54, 31: 55, 32: 56, 33: 57, 34: 58, 35: 60, 36: 61, 37: 63, 38: 65, 39: 67, 40: 68
};

const READING_TABLE = {
  0: 21, 1: 22, 2: 23, 3: 23, 4: 24, 5: 25, 6: 26, 7: 27, 8: 28, 9: 28,
  10: 29, 11: 30, 12: 31, 13: 32, 14: 34, 15: 35, 16: 36, 17: 37, 18: 38, 19: 39,
  20: 40, 21: 41, 22: 42, 23: 43, 24: 43, 25: 44, 26: 45, 27: 46, 28: 46, 29: 47,
  30: 48, 31: 48, 32: 49, 33: 50, 34: 51, 35: 52, 36: 52, 37: 53, 38: 54, 39: 54,
  40: 55, 41: 56, 42: 57, 43: 58, 44: 59, 45: 60, 46: 61, 47: 63, 48: 65, 49: 66, 50: 67
};

function calcScaled(raw, total, maxScale, table) {
  if (total <= 0) return table[0] || 20;
  const normalized = Math.min(maxScale, Math.max(0, Math.round((raw / total) * maxScale)));
  return table[normalized] || table[0];
}

// 1. SUBMIT ATTEMPT (CREATE)
exports.submitAttempt = async (req, res) => {
  try {
    const { package_id, user_answers, time_spent_seconds } = req.body;
    const userId = req.user.id;

    const pkg = await TestPackage.findByPk(package_id, {
      include: [{ model: Question, as: 'questions' }]
    });

    if (!pkg) {
      return res.status(404).json({ success: false, message: 'Paket tes tidak ditemukan.' });
    }

    const answers = user_answers || {};
    let p1Correct = 0, p1Total = 0;
    let p2Correct = 0, p2Total = 0;
    let p3Correct = 0, p3Total = 0;

    pkg.questions.forEach(q => {
      if (q.part === 1) p1Total++;
      if (q.part === 2) p2Total++;
      if (q.part === 3) p3Total++;

      const userAns = answers[q.id];
      if (userAns && userAns.toLowerCase() === q.correct_answer.toLowerCase()) {
        if (q.part === 1) p1Correct++;
        if (q.part === 2) p2Correct++;
        if (q.part === 3) p3Correct++;
      }
    });

    const lScaled = calcScaled(p1Correct, p1Total, 50, LISTENING_TABLE);
    const sScaled = calcScaled(p2Correct, p2Total, 40, STRUCTURE_TABLE);
    const rScaled = calcScaled(p3Correct, p3Total, 50, READING_TABLE);

    const totalScore = Math.min(677, Math.max(310, Math.round(((lScaled + sScaled + rScaled) * 10) / 3)));
    const isPassed = totalScore >= 450;
    const scoreGap = totalScore - 450;

    const attemptId = `attempt-${Date.now()}-${Math.floor(Math.random() * 1000)}`;

    const newAttempt = await TestAttempt.create({
      id: attemptId,
      user_id: userId,
      package_id: pkg.id,
      package_title: pkg.title,
      listening_raw: p1Correct,
      listening_total: p1Total,
      listening_scaled: lScaled,
      structure_raw: p2Correct,
      structure_total: p2Total,
      structure_scaled: sScaled,
      reading_raw: p3Correct,
      reading_total: p3Total,
      reading_scaled: rScaled,
      total_score: totalScore,
      is_passed: isPassed,
      score_gap: scoreGap,
      time_spent_seconds: time_spent_seconds || 0,
      user_answers_json: JSON.stringify(answers),
      attempt_date: new Date()
    });

    res.status(201).json({
      success: true,
      message: isPassed ? 'Selamat! Anda melampaui batas minimal 450.' : 'Ujian selesai. Tetap semangat berlatih!',
      data: newAttempt
    });
  } catch (error) {
    console.error('submitAttempt error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
};

// 2. GET USER ATTEMPTS (READ with Search)
exports.getUserAttempts = async (req, res) => {
  try {
    const userId = req.user.id;
    const { search } = req.query;

    const where = { user_id: userId };
    if (search) {
      where.package_title = { [Op.like]: `%${search}%` };
    }

    const attempts = await TestAttempt.findAll({
      where,
      order: [['attempt_date', 'DESC']]
    });

    res.json({ success: true, data: attempts });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 3. GET ATTEMPT DETAIL BY ID (READ)
exports.getAttemptById = async (req, res) => {
  try {
    const { id } = req.params;
    const attempt = await TestAttempt.findByPk(id, {
      include: [
        {
          model: TestPackage,
          as: 'package',
          include: [{ model: Question, as: 'questions' }]
        }
      ]
    });

    if (!attempt) {
      return res.status(404).json({ success: false, message: 'Riwayat tes tidak ditemukan.' });
    }

    // Security check: only owner or dosen can view
    if (attempt.user_id !== req.user.id && req.user.role !== 'dosen') {
      return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }

    res.json({ success: true, data: attempt });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 4. DELETE ATTEMPT
exports.deleteAttempt = async (req, res) => {
  try {
    const { id } = req.params;
    const attempt = await TestAttempt.findByPk(id);

    if (!attempt) {
      return res.status(404).json({ success: false, message: 'Riwayat tes tidak ditemukan.' });
    }

    if (attempt.user_id !== req.user.id && req.user.role !== 'dosen') {
      return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }

    await attempt.destroy();
    res.json({ success: true, message: 'Riwayat tes berhasil dihapus.' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

