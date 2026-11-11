const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const TestAttempt = sequelize.define('TestAttempt', {
  id: {
    type: DataTypes.STRING,
    primaryKey: true
  },
  user_id: {
    type: DataTypes.INTEGER,
    allowNull: false
  },
  package_id: {
    type: DataTypes.STRING,
    allowNull: false
  },
  package_title: {
    type: DataTypes.STRING,
    allowNull: false
  },
  listening_raw: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  listening_total: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  listening_scaled: {
    type: DataTypes.INTEGER,
    defaultValue: 24
  },
  structure_raw: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  structure_total: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  structure_scaled: {
    type: DataTypes.INTEGER,
    defaultValue: 20
  },
  reading_raw: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  reading_total: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  reading_scaled: {
    type: DataTypes.INTEGER,
    defaultValue: 21
  },
  total_score: {
    type: DataTypes.INTEGER,
    defaultValue: 310
  },
  is_passed: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  },
  score_gap: {
    type: DataTypes.INTEGER,
    defaultValue: -140
  },
  time_spent_seconds: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  user_answers_json: {
    type: DataTypes.TEXT, // JSON string of questionId -> answer
    allowNull: true
  },
  attempt_date: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'test_attempts',
  timestamps: true
});

module.exports = TestAttempt;

