const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { Op } = require('sequelize');
const { User } = require('../models');

const generateTokens = (user) => {
  const secret = process.env.JWT_SECRET || 'toefl_ept_super_secret_jwt_key_0100';
  const refreshSecret = process.env.JWT_REFRESH_SECRET || 'toefl_ept_refresh_token_secret_key_0100';

  const accessToken = jwt.sign(
    { id: user.id, email: user.email, role: user.role, nim: user.nim },
    secret,
    { expiresIn: process.env.JWT_EXPIRES_IN || '15m' }
  );

  const refreshToken = jwt.sign(
    { id: user.id },
    refreshSecret,
    { expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || '7d' }
  );

  return { accessToken, refreshToken };
};

// 1. REGISTER
exports.register = async (req, res) => {
  try {
    const { nama_lengkap, nim, email, password, role } = req.body;

    if (!nama_lengkap || !nim || !email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Semua kolom (nama_lengkap, nim, email, password) wajib diisi.'
      });
    }

    // Check duplicate
    const existing = await User.findOne({
      where: {
        [Op.or]: [{ email }, { nim }]
      }
    });

    if (existing) {
      return res.status(400).json({
        success: false,
        message: 'Email atau NIM sudah terdaftar dalam sistem.'
      });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const assignedRole = (role === 'dosen') ? 'dosen' : 'mahasiswa';

    const newUser = await User.create({
      nama_lengkap,
      nim,
      email,
      password: hashedPassword,
      role: assignedRole
    });

    const tokens = generateTokens(newUser);
    newUser.refresh_token = tokens.refreshToken;
    await newUser.save();

    res.status(201).json({
      success: true,
      message: 'Pendaftaran akun berhasil!',
      data: {
        user: {
          id: newUser.id,
          nama_lengkap: newUser.nama_lengkap,
          nim: newUser.nim,
          email: newUser.email,
          role: newUser.role
        },
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken
      }
    });
  } catch (error) {
    console.error('Register error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
};

// 2. LOGIN (JWT Access Token + Refresh Token)
exports.login = async (req, res) => {
  try {
    const { emailOrNim, password } = req.body;

    if (!emailOrNim || !password) {
      return res.status(400).json({
        success: false,
        message: 'Email/NIM dan kata sandi wajib diisi.'
      });
    }

    const user = await User.findOne({
      where: {
        [Op.or]: [{ email: emailOrNim }, { nim: emailOrNim }]
      }
    });

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Kredensial tidak valid. Akun tidak ditemukan.'
      });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'Kata sandi salah.'
      });
    }

    const tokens = generateTokens(user);
    user.refresh_token = tokens.refreshToken;
    await user.save();

    res.json({
      success: true,
      message: 'Login berhasil!',
      data: {
        user: {
          id: user.id,
          nama_lengkap: user.nama_lengkap,
          nim: user.nim,
          email: user.email,
          role: user.role
        },
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken
      }
    });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
};

// 3. REFRESH TOKEN
exports.refreshToken = async (req, res) => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken) {
      return res.status(400).json({ success: false, message: 'Refresh token wajib disertakan.' });
    }

    const refreshSecret = process.env.JWT_REFRESH_SECRET || 'toefl_ept_refresh_token_secret_key_0100';

    jwt.verify(refreshToken, refreshSecret, async (err, decoded) => {
      if (err) {
        return res.status(403).json({ success: false, message: 'Refresh token tidak valid atau kedaluwarsa.' });
      }

      const user = await User.findOne({
        where: { id: decoded.id, refresh_token: refreshToken }
      });

      if (!user) {
        return res.status(403).json({ success: false, message: 'Sesi login telah berakhir.' });
      }

      const tokens = generateTokens(user);
      user.refresh_token = tokens.refreshToken;
      await user.save();

      res.json({
        success: true,
        message: 'Token berhasil diperbarui.',
        data: {
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken
        }
      });
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 4. GET CURRENT USER PROFILE
exports.getProfile = async (req, res) => {
  res.json({
    success: true,
    data: req.user
  });
};

// 5. UPDATE PROFILE
exports.updateProfile = async (req, res) => {
  try {
    const { nama_lengkap, email, password } = req.body;
    const user = await User.findByPk(req.user.id);

    if (nama_lengkap) user.nama_lengkap = nama_lengkap;
    if (email) user.email = email;
    if (password) {
      user.password = await bcrypt.hash(password, 10);
    }

    await user.save();

    res.json({
      success: true,
      message: 'Profil berhasil diperbarui.',
      data: {
        id: user.id,
        nama_lengkap: user.nama_lengkap,
        nim: user.nim,
        email: user.email,
        role: user.role
      }
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 6. DELETE USER (Deactivate)
exports.deleteUser = async (req, res) => {
  try {
    const user = await User.findByPk(req.user.id);
    await user.destroy();
    res.json({ success: true, message: 'Akun berhasil dihapus.' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// 7. SEARCH USERS (Khusus Dosen/Admin)
exports.searchUsers = async (req, res) => {
  try {
    const { q } = req.query;
    const where = {};
    if (q) {
      where[Op.or] = [
        { nama_lengkap: { [Op.like]: `%${q}%` } },
        { nim: { [Op.like]: `%${q}%` } },
        { email: { [Op.like]: `%${q}%` } }
      ];
    }

    const users = await User.findAll({
      where,
      attributes: ['id', 'nama_lengkap', 'nim', 'email', 'role', 'createdAt'],
      order: [['createdAt', 'DESC']]
    });

    res.json({ success: true, data: users });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

