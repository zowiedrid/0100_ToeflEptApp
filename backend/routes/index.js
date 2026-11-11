const express = require('express');
const router = express.Router();

const authRoutes = require('./authRoutes');
const packageRoutes = require('./packageRoutes');
const questionRoutes = require('./questionRoutes');
const attemptRoutes = require('./attemptRoutes');
const bookmarkRoutes = require('./bookmarkRoutes');

router.use('/auth', authRoutes);
router.use('/packages', packageRoutes);
router.use('/questions', questionRoutes);
router.use('/attempts', attemptRoutes);
router.use('/bookmarks', bookmarkRoutes);

// Health check endpoint
router.get('/health', (req, res) => {
  res.json({
    status: 'ok',
    message: 'TOEFL EPT REST API Server is running',
    timestamp: new Date().toISOString()
  });
});

module.exports = router;

