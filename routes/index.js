const express = require('express');
const { uploadProfileImage} = require('../controller/uploadcontroller');
const { register, login, getUser } = require('../controller/authcontroller');
const { saveScanHistory, getScanHistory } = require('../controller/scancontroller');
const verifyAuthMiddleware = require('../middleware/verifyAuthMiddleware');
const upload = require('../middleware/uploadMiddleware');
const router = express.Router();

router.get('/', (req, res) => {
    res.send('Welcome to the homepage!');
  });

router.post('/api/uploadprofile', verifyAuthMiddleware,upload.single('file'), uploadProfileImage);

router.post('/api/register', register);
router.post('/api/login', login);
router.get('/api/getuser', getUser);

router.get('/api/history', verifyAuthMiddleware, getScanHistory);
router.post('/api/history', verifyAuthMiddleware, upload.single('file'), saveScanHistory);

module.exports = router;
