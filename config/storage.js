const { Storage } = require('@google-cloud/storage');
const path = require('path');

const storage = new Storage({
  keyFilename: path.join(__dirname, 'key-file-name'),
  projectId: 'your-project-id'
});

const bucketName = 'your-name-storage';
const bucket = storage.bucket(bucketName);

module.exports = bucket;
