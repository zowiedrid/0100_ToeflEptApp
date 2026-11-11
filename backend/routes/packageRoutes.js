const express = require('express');
const router = express.Router();
const packageController = require('../controllers/packageController');
const { verifyToken, requireRole } = require('../middlewares/auth');

router.get('/', verifyToken, packageController.getPackages);
router.get('/:id', verifyToken, packageController.getPackageById);

// Khusus Dosen: C-U-D Paket
router.post('/', verifyToken, requireRole('dosen'), packageController.createPackage);
router.put('/:id', verifyToken, requireRole('dosen'), packageController.updatePackage);
router.delete('/:id', verifyToken, requireRole('dosen'), packageController.deletePackage);

module.exports = router;

