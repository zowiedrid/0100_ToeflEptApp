const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const TestPackage = sequelize.define('TestPackage', {
  id: {
    type: DataTypes.STRING,
    primaryKey: true
  },
  title: {
    type: DataTypes.STRING,
    allowNull: false
  },
  short_description: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  badge: {
    type: DataTypes.STRING,
    defaultValue: 'Soal Latihan EPT'
  },
  target_focus: {
    type: DataTypes.STRING,
    allowNull: true
  },
  duration_minutes: {
    type: DataTypes.INTEGER,
    defaultValue: 115
  },
  created_by: {
    type: DataTypes.INTEGER,
    allowNull: true
  }
}, {
  tableName: 'test_packages',
  timestamps: true
});

module.exports = TestPackage;

