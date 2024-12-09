const { Storage } = require('@google-cloud/storage');
const path = require('path');

const storage = new Storage({
    keyFilename: path.join(__dirname, 'key-file'),
    projectId: 'your-project-id'
  });

  const bucketName = 'bucket-name';
  const bucket = storage.bucket(bucketName);

const uploadFileToBucket = (file) => {
  return new Promise((resolve, reject) => {
    const blob = bucket.file(Date.now() + '-' + file.originalname); 
    const blobStream = blob.createWriteStream({
      resumable: false,
    });

    blobStream.on('error', (err) => reject(err));
    blobStream.on('finish', () => resolve(`https://storage.googleapis.com/${bucket.name}/${blob.name}`));

    blobStream.end(file.buffer);
  });
};

module.exports = { uploadFileToBucket };
