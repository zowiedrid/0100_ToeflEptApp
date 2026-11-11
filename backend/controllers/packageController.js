const { Op } = require('sequelize');
const { TestPackage, Question } = require('../models');

// 1. GET ALL PACKAGES (with Search)
exports.getPackages = async (req, res) => {
  try {
    const { search } = req.query;
    const where = {};

    if (search) {
      where[Op.or] = [
        { title: { [Op.like]: `%${search}%` } },
        { short_description: { [Op.like]: `%${search}%` } },
        { badge: { [Op.like]: `%${search}%` } }
      ];
    }

    const packages = await TestPackage.findAll({
      where,
      include: [
        {
          model: Question,
          as: 'questions',
          attributes: ['id', 'part', 'number', 'type']
        }
      ],
      order: [['createdAt', 'ASC']]
    });

    // Attach total question count
    const formatted = packages.map(pkg => {
      const p = pkg.toJSON();
      p.totalQuestions = p.questions ? p.questions.length : 0;
      return p;
    });

    res.json({
      success: true,
      data: formatted
    });
  } catch (error) {
    console.error('getPackages error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
};

// 2. GET PACKAGE DETAIL BY ID (with full questions)
exports.getPackageById = async (req, res) => {
  try {
    const { id } = req.params;
    const pkg = await TestPackage.findByPk(id, {
      include: [
        {
          model: Question,
          as: 'questions'
        }
      ],
      order: [[{ model: Question, as: 'questions' }, 'number', 'ASC']]
    });

    if (!pkg) {
      return res.status(404).json({ success: false, message: 'Paket tes tidak ditemukan.' });
    }

    res.json({ success: true, data: pkg });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 3. CREATE PACKAGE (Khusus Dosen)
exports.createPackage = async (req, res) => {
  try {
    const { id, title, short_description, badge, target_focus, duration_minutes } = req.body;

    if (!id || !title || !short_description) {
      return res.status(400).json({
        success: false,
        message: 'ID, judul, dan deskripsi singkat wajib diisi.'
      });
    }

    const existing = await TestPackage.findByPk(id);
    if (existing) {
      return res.status(400).json({ success: false, message: 'ID paket sudah digunakan.' });
    }

    const newPackage = await TestPackage.create({
      id,
      title,
      short_description,
      badge: badge || 'Soal Latihan EPT',
      target_focus,
      duration_minutes: duration_minutes || 115,
      created_by: req.user.id
    });

    res.status(201).json({
      success: true,
      message: 'Paket soal berhasil ditambahkan.',
      data: newPackage
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 4. UPDATE PACKAGE (Khusus Dosen)
exports.updatePackage = async (req, res) => {
  try {
    const { id } = req.params;
    const { title, short_description, badge, target_focus, duration_minutes } = req.body;

    const pkg = await TestPackage.findByPk(id);
    if (!pkg) {
      return res.status(404).json({ success: false, message: 'Paket soal tidak ditemukan.' });
    }

    if (title) pkg.title = title;
    if (short_description) pkg.short_description = short_description;
    if (badge) pkg.badge = badge;
    if (target_focus !== undefined) pkg.target_focus = target_focus;
    if (duration_minutes) pkg.duration_minutes = duration_minutes;

    await pkg.save();

    res.json({
      success: true,
      message: 'Paket soal berhasil diperbarui.',
      data: pkg
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 5. DELETE PACKAGE (Khusus Dosen)
exports.deletePackage = async (req, res) => {
  try {
    const { id } = req.params;
    const pkg = await TestPackage.findByPk(id);
    if (!pkg) {
      return res.status(404).json({ success: false, message: 'Paket soal tidak ditemukan.' });
    }

    await pkg.destroy();
    res.json({ success: true, message: 'Paket soal berhasil dihapus.' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

