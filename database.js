const { Sequelize } = require('sequelize');

const database = new Sequelize('name', 'user', 'password', {
  host: 'host',
  dialect: 'mysql',
  dialectOptions: {
  }
});

  module.exports = database;