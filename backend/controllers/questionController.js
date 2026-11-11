const { Op } = require('sequelize');
const { Question, TestPackage } = require('../models');

// 1. GET QUESTIONS (with filters & search)
exports.getQuestions = async (req, res) => {
  try {
    const { package_id, part, search } = req.query;
    const where = {};

    if (package_id) where.package_id = package_id;
    if (part) where.part = Number(part);
    if (search) {
      where[Op.or] = [
        { prompt: { [Op.like]: `%${search}%` } },
        { explanation: { [Op.like]: `%${search}%` } }
      ];
    }

    const questions = await Question.findAll({
      where,
      order: [['part', 'ASC'], ['number', 'ASC']]
    });

    res.json({ success: true, data: questions });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 2. GET QUESTION BY ID
exports.getQuestionById = async (req, res) => {
  try {
    const { id } = req.params;
    const question = await Question.findByPk(id);
    if (!question) {
      return res.status(404).json({ success: false, message: 'Soal tidak ditemukan.' });
    }
    res.json({ success: true, data: question });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 3. CREATE QUESTION (Khusus Dosen)
exports.createQuestion = async (req, res) => {
  try {
    const {
      id,
      package_id,
      number,
      part,
      type,
      prompt,
      option_a,
      option_b,
      option_c,
      option_d,
      correct_answer,
      explanation,
      audio_script,
      audio_speaker,
      passage_text
    } = req.body;

    if (!id || !package_id || !prompt || !option_a || !option_b || !option_c || !option_d || !correct_answer) {
      return res.status(400).json({
        success: false,
        message: 'Field id, package_id, prompt, opsi a-d, dan correct_answer wajib diisi.'
      });
    }

    const pkg = await TestPackage.findByPk(package_id);
    if (!pkg) {
      return res.status(404).json({ success: false, message: 'Paket tes tidak ditemukan.' });
    }

    const newQuestion = await Question.create({
      id,
      package_id,
      number: Number(number) || 1,
      part: Number(part) || 1,
      type: type || 'structure',
      prompt,
      option_a,
      option_b,
      option_c,
      option_d,
      correct_answer: correct_answer.toLowerCase(),
      explanation,
      audio_script,
      audio_speaker,
      passage_text
    });

    res.status(201).json({
      success: true,
      message: 'Soal berhasil ditambahkan.',
      data: newQuestion
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 4. UPDATE QUESTION (Khusus Dosen)
exports.updateQuestion = async (req, res) => {
  try {
    const { id } = req.params;
    const question = await Question.findByPk(id);
    if (!question) {
      return res.status(404).json({ success: false, message: 'Soal tidak ditemukan.' });
    }

    const {
      prompt,
      option_a,
      option_b,
      option_c,
      option_d,
      correct_answer,
      explanation,
      audio_script,
      passage_text
    } = req.body;

    if (prompt) question.prompt = prompt;
    if (option_a) question.option_a = option_a;
    if (option_b) question.option_b = option_b;
    if (option_c) question.option_c = option_c;
    if (option_d) question.option_d = option_d;
    if (correct_answer) question.correct_answer = correct_answer.toLowerCase();
    if (explanation !== undefined) question.explanation = explanation;
    if (audio_script !== undefined) question.audio_script = audio_script;
    if (passage_text !== undefined) question.passage_text = passage_text;

    await question.save();

    res.json({
      success: true,
      message: 'Soal berhasil diperbarui.',
      data: question
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 5. DELETE QUESTION (Khusus Dosen)
exports.deleteQuestion = async (req, res) => {
  try {
    const { id } = req.params;
    const question = await Question.findByPk(id);
    if (!question) {
      return res.status(404).json({ success: false, message: 'Soal tidak ditemukan.' });
    }

    await question.destroy();
    res.json({ success: true, message: 'Soal berhasil dihapus.' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

