const { ScanHistories } = require('../models');
const { uploadImageToStorage } = require('../services/storageService');

const saveScanHistory = async (req, res) => {
  try {
    const imageUrl = await uploadImageToStorage(req.file);
    const { result } = req.body;

    const scanHistory = await ScanHistories.create({
      userId: req.user.id,
      imageUrl,
      result,
    });

    res.status(201).json(scanHistory);
  } catch (error) {
    console.error('Error saving scan history:', error.message);
    res.status(500).json({ error: 'Failed to save scan history' });
  }
};

const getScanHistory = async (req, res) => {
  try {
    const scans = await ScanHistories.findAll({
      where: { userId: req.user.id },
      order: [['createdAt', 'DESC']],
    });

    res.status(200).json({
      message: 'Scan history retrieved successfully',
      data: scans,
    });
  } catch (err) {
    console.error('Error fetching scan history:', err.message);
    res.status(500).json({ error: 'Failed to retrieve scan history' });
  }
};

module.exports = { saveScanHistory, getScanHistory };
