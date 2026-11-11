const { Op } = require('sequelize');
const { Bookmark, Question } = require('../models');

// 1. GET BOOKMARKS (with Search)
exports.getBookmarks = async (req, res) => {
  try {
    const userId = req.user.id;
    const { search } = req.query;

    const where = { user_id: userId };
    const questionWhere = {};

    if (search) {
      where[Op.or] = [
        { notes: { [Op.like]: `%${search}%` } },
        { '$question.prompt$': { [Op.like]: `%${search}%` } }
      ];
    }

    const bookmarks = await Bookmark.findAll({
      where,
      include: [
        {
          model: Question,
          as: 'question'
        }
      ],
      order: [['createdAt', 'DESC']]
    });

    res.json({ success: true, data: bookmarks });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 2. CREATE BOOKMARK
exports.createBookmark = async (req, res) => {
  try {
    const userId = req.user.id;
    const { question_id, notes } = req.body;

    if (!question_id) {
      return res.status(400).json({ success: false, message: 'question_id wajib disertakan.' });
    }

    const question = await Question.findByPk(question_id);
    if (!question) {
      return res.status(404).json({ success: false, message: 'Soal tidak ditemukan.' });
    }

    const [bookmark, created] = await Bookmark.findOrCreate({
      where: { user_id: userId, question_id },
      defaults: {
        user_id: userId,
        question_id,
        notes: notes || ''
      }
    });

    if (!created && notes) {
      bookmark.notes = notes;
      await bookmark.save();
    }

    res.status(201).json({
      success: true,
      message: 'Soal berhasil disimpan ke daftar bookmark.',
      data: bookmark
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 3. UPDATE BOOKMARK NOTE
exports.updateBookmark = async (req, res) => {
  try {
    const { id } = req.params;
    const { notes } = req.body;

    const bookmark = await Bookmark.findOne({
      where: { id, user_id: req.user.id }
    });

    if (!bookmark) {
      return res.status(404).json({ success: false, message: 'Bookmark tidak ditemukan.' });
    }

    bookmark.notes = notes || '';
    await bookmark.save();

    res.json({
      success: true,
      message: 'Catatan belajar berhasil diperbarui.',
      data: bookmark
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 4. DELETE BOOKMARK
exports.deleteBookmark = async (req, res) => {
  try {
    const { id } = req.params;

    const bookmark = await Bookmark.findOne({
      where: { id, user_id: req.user.id }
    });

    if (!bookmark) {
      return res.status(404).json({ success: false, message: 'Bookmark tidak ditemukan.' });
    }

    await bookmark.destroy();
    res.json({ success: true, message: 'Bookmark berhasil dihapus.' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

