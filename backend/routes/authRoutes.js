const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { verifyToken, requireRole } = require('../middlewares/auth');

router.post('/register', authController.register);
router.post('/login', authController.login);
router.post('/refresh', authController.refreshToken);

router.get('/profile', verifyToken, authController.getProfile);
router.put('/profile', verifyToken, authController.updateProfile);
router.delete('/profile', verifyToken, authController.deleteUser);

// Khusus Dosen: Search users / Mahasiswa
router.get('/users', verifyToken, requireRole('dosen'), authController.searchUsers);

module.exports = router;

