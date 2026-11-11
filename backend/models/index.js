const sequelize = require('../config/database');
const User = require('./User');
const TestPackage = require('./TestPackage');
const Question = require('./Question');
const TestAttempt = require('./TestAttempt');
const Bookmark = require('./Bookmark');

// Associations
// 1. User <-> TestAttempt
User.hasMany(TestAttempt, { foreignKey: 'user_id', as: 'attempts', onDelete: 'CASCADE' });
TestAttempt.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

// 2. TestPackage <-> Question
TestPackage.hasMany(Question, { foreignKey: 'package_id', as: 'questions', onDelete: 'CASCADE' });
Question.belongsTo(TestPackage, { foreignKey: 'package_id', as: 'package' });

// 3. TestPackage <-> TestAttempt
TestPackage.hasMany(TestAttempt, { foreignKey: 'package_id', as: 'attempts', onDelete: 'CASCADE' });
TestAttempt.belongsTo(TestPackage, { foreignKey: 'package_id', as: 'package' });

// 4. User <-> Bookmark
User.hasMany(Bookmark, { foreignKey: 'user_id', as: 'bookmarks', onDelete: 'CASCADE' });
Bookmark.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

// 5. Question <-> Bookmark
Question.hasMany(Bookmark, { foreignKey: 'question_id', as: 'bookmarks', onDelete: 'CASCADE' });
Bookmark.belongsTo(Question, { foreignKey: 'question_id', as: 'question' });

module.exports = {
  sequelize,
  User,
  TestPackage,
  Question,
  TestAttempt,
  Bookmark
};

