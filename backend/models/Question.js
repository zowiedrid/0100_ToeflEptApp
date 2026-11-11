const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Question = sequelize.define('Question', {
  id: {
    type: DataTypes.STRING,
    primaryKey: true
  },
  package_id: {
    type: DataTypes.STRING,
    allowNull: false
  },
  number: {
    type: DataTypes.INTEGER,
    allowNull: false
  },
  part: {
    type: DataTypes.INTEGER, // 1 = Listening, 2 = Structure, 3 = Reading
    allowNull: false
  },
  type: {
    type: DataTypes.STRING, // listening, structure, reading
    allowNull: false
  },
  prompt: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  option_a: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  option_b: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  option_c: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  option_d: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  correct_answer: {
    type: DataTypes.CHAR(1), // a, b, c, d
    allowNull: false
  },
  explanation: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  audio_script: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  audio_speaker: {
    type: DataTypes.STRING,
    allowNull: true
  },
  passage_text: {
    type: DataTypes.TEXT,
    allowNull: true
  }
}, {
  tableName: 'questions',
  timestamps: true
});

module.exports = Question;

