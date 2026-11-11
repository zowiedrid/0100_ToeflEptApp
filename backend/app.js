require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { sequelize } = require('./models');
const apiRoutes = require('./routes');

const app = express();
const PORT = process.env.PORT || 5000;

// Middlewares
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Request logging in development
if (process.env.NODE_ENV !== 'production') {
  app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    next();
  });
}

// API Routes
app.use('/api', apiRoutes);

// Root route
app.get('/', (req, res) => {
  res.json({
    name: 'TOEFL EPT Preparation REST API',
    version: '1.0.0',
    developer: 'Ilham Fadhilah (20200140100)',
    endpoints: {
      auth: '/api/auth (login, register, refresh, profile)',
      packages: '/api/packages (CRUDS paket soal)',
      questions: '/api/questions (CRUDS butir soal)',
      attempts: '/api/attempts (CRUDS ujian & riwayat nilai)',
      bookmarks: '/api/bookmarks (CRUDS catatan soal sulit)'
    }
  });
});

// Database Sync and Server Listen
async function startServer() {
  try {
    await sequelize.authenticate();
    console.log('✓ Database connection established successfully.');

    // Sync database tables
    await sequelize.sync({ alter: true });
    console.log('✓ Database models synchronized.');

    app.listen(PORT, '0.0.0.0', () => {
      console.log(`🚀 TOEFL EPT REST API Server is running on http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error('✗ Unable to start server:', error);
  }
}

if (require.main === module) {
  startServer();
}

module.exports = app;

