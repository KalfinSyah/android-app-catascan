const { uploadFileToBucket } = require('../services/uploadServices');
const { User } = require('../models');

const uploadProfileImage = async (req, res) => {
  const userId = req.user.id;
  const imageFile = req.file;

  if (!imageFile) {
    return res.status(400).json({ error: 'Image file is required' });
  }

  try {
    const imageUrl = await uploadFileToBucket(imageFile);

    const user = await User.findByPk(userId);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    user.profileImageUrl = imageUrl;
    await user.save();

    res.status(200).json({
      message: 'Profile image updated successfully',
      profileImageUrl: imageUrl,
    });
  } catch (error) {
    console.error('Error uploading profile image:', error);
    res.status(500).json({ error: 'Failed to upload profile image' });
  }
};

module.exports = { uploadProfileImage };
