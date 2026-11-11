require('dotenv').config();
const { Sequelize } = require('sequelize');
const path = require('path');

const dialect = process.env.DB_DIALECT || 'sqlite';

let sequelize;

if (dialect === 'sqlite') {
  const storagePath = process.env.DB_STORAGE 
    ? path.resolve(__dirname, '..', process.env.DB_STORAGE) 
    : path.resolve(__dirname, '../database.sqlite');

  sequelize = new Sequelize({
    dialect: 'sqlite',
    storage: storagePath,
    logging: false
  });
} else {
  sequelize = new Sequelize(
    process.env.DB_NAME || 'toefl_ept_db',
    process.env.DB_USER || 'root',
    process.env.DB_PASS || '',
    {
      host: process.env.DB_HOST || 'localhost',
      port: Number(process.env.DB_PORT) || (dialect === 'postgres' ? 5432 : 3306),
      dialect: dialect,
      logging: false,
      pool: {
        max: 5,
        min: 0,
        acquire: 30000,
        idle: 10000
      }
    }
  );
}

module.exports = sequelize;

