const jwt = require('jsonwebtoken');
const { User } = require('../models');

const authenticate = async (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ error: 'Token is required' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findByPk(decoded.id);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    req.user = { id: user.id };
    next();
  } catch (err) {
    console.error('Authentication error:', err.message);
    res.status(401).json({ error: 'Invalid token' });
  }
};

module.exports = authenticate;
