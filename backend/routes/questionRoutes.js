const express = require('express');
const router = express.Router();
const questionController = require('../controllers/questionController');
const { verifyToken, requireRole } = require('../middlewares/auth');

router.get('/', verifyToken, questionController.getQuestions);
router.get('/:id', verifyToken, questionController.getQuestionById);

// Khusus Dosen: C-U-D Soal
router.post('/', verifyToken, requireRole('dosen'), questionController.createQuestion);
router.put('/:id', verifyToken, requireRole('dosen'), questionController.updateQuestion);
router.delete('/:id', verifyToken, requireRole('dosen'), questionController.deleteQuestion);

module.exports = router;

