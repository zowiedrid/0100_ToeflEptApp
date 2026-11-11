const express = require('express');
const router = express.Router();
const attemptController = require('../controllers/attemptController');
const { verifyToken } = require('../middlewares/auth');

router.post('/', verifyToken, attemptController.submitAttempt);
router.get('/', verifyToken, attemptController.getUserAttempts);
router.get('/:id', verifyToken, attemptController.getAttemptById);
router.delete('/:id', verifyToken, attemptController.deleteAttempt);

module.exports = router;

