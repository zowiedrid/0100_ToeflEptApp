const jwt = require('jsonwebtoken');
const { User } = require('../models');

const verifyToken = async (req, res, next) => {
  try {
    const authHeader = req.headers['authorization'];
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        success: false,
        message: 'Akses ditolak. Token otentikasi tidak ditemukan.'
      });
    }

    const token = authHeader.split(' ')[1];
    const secret = process.env.JWT_SECRET || 'toefl_ept_super_secret_jwt_key_0100';

    jwt.verify(token, secret, async (err, decoded) => {
      if (err) {
        if (err.name === 'TokenExpiredError') {
          return res.status(401).json({
            success: false,
            message: 'Token sudah kedaluwarsa. Silakan refresh token.',
            isExpired: true
          });
        }
        return res.status(403).json({
          success: false,
          message: 'Token tidak valid.'
        });
      }

      const user = await User.findByPk(decoded.id, {
        attributes: ['id', 'nama_lengkap', 'nim', 'email', 'role']
      });

      if (!user) {
        return res.status(404).json({
          success: false,
          message: 'Pengguna pemilik token tidak ditemukan.'
        });
      }

      req.user = user;
      next();
    });
  } catch (error) {
    console.error('Auth middleware error:', error);
    res.status(500).json({ success: false, message: 'Kesalahan internal server pada otentikasi.' });
  }
};

const requireRole = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user || !allowedRoles.includes(req.user.role)) {
      return res.status(403).json({
        success: false,
        message: `Akses ditolak. Fitur ini hanya dapat diakses oleh peran: ${allowedRoles.join(', ')}.`
      });
    }
    next();
  };
};

module.exports = {
  verifyToken,
  requireRole
};

