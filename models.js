const { Sequelize } = require('sequelize');
const sequelize = require('./database');

(async () => {
  try {
    await sequelize.authenticate();
    console.log('Connected to database...');
  } catch (error) {
    console.log('Unable to connect to database: ', error);
  }
})();

const database = {};

database.Sequelize = Sequelize;
database.sequelize = sequelize;

database.sequelize.sync({ force: false });

database.User = require('./models/users')(sequelize, Sequelize);
database.ScanHistories = require('./models/scanHistories')(sequelize, Sequelize);

database.ScanHistories.belongsTo(database.User, {
  foreignKey: 'id',
});
database.User.hasMany(database.ScanHistories);

module.exports = database;