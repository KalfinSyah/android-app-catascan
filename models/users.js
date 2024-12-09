'use strict';
const { Model } = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class User extends Model {
    static associate(models) {
      User.hasMany(models.ScanHistories, { foreignKey: 'userId' });
    }
  }

  User.init(
    {
      name: {
        type: DataTypes.STRING,
        allowNull: false
      },
      email: {
        type: DataTypes.STRING,
        allowNull: false
      },
      password: {
        type: DataTypes.STRING,
        allowNull: false
      },
      profileImageUrl: {
        type: DataTypes.STRING,
        allowNull: true
      },
    },
    { sequelize, modelName: 'User' }
  );

  return User;
};
