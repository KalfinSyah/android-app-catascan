'use strict';
const { Model } = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class ScanHistories extends Model {
    static associate(models) {
      ScanHistories.belongsTo(models.User, { foreignKey: 'userId' });
    }
  }
  ScanHistories.init(
    {
      userId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'Users',
          key: 'id',
        },
      },
      imageUrl: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      result: {
        type: DataTypes.STRING,
        allowNull: false,
      },
    },
    {
      sequelize,
      modelName: 'ScanHistories',
    }
  );
  return ScanHistories;
};
