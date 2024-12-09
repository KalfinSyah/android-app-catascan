const { Storage } = require('@google-cloud/storage');
const path = require('path');

const storage = new Storage({
  keyFilename: path.join(__dirname, 'key-file'),
  projectId: 'your-projectid'
});

const bucketName = 'bucket-name';
const bucket = storage.bucket(bucketName);

const uploadImageToStorage = (file) => {
  return new Promise((resolve, reject) => {
    if (!file) {
      reject('No file provided');
    }

    const blob = bucket.file(file.originalname);
    const blobStream = blob.createWriteStream({
      resumable: false,
      contentType: file.mimetype,
    });

    blobStream.on('finish', () => {
      const publicUrl = `https://storage.googleapis.com/${bucketName}/${blob.name}`;
      resolve(publicUrl);
    });

    blobStream.on('error', (err) => {
      reject(err);
    });

    blobStream.end(file.buffer);
  });
};

module.exports = { uploadImageToStorage };
