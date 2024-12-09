const express = require('express');
const routes = require('./routes/index');
const app = express();
require('dotenv').config();

app.use(express.json());

app.use('/', routes);

const PORT = process.env.PORT || 8080;

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
