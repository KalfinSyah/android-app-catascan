# Catascan
Catascan is an innovative Android application designed to detect and classify the stages of cataracts, such as normal, immature, and mature, using machine learning technology. This project aims to empower vulnerable communities by providing a cost-effective and accessible tool for early cataract detection, promoting timely medical intervention and reducing the prevalence of vision loss due to cataracts.

# Development Setup
This repository contains the backend RESTful API for the Catascan application, which serves as the interface for performing Create, Read, Update, and Delete (CRUD) operations on the application's database. By leveraging this API, the application can seamlessly interact with the database, enabling users to store, retrieve, update, and delete data related to Catascan. Overall, this repository plays a pivotal role in supporting the functionality and data management of the PCOS Pal application.

## Prerequisites
- NodeJS
- Node Package Manager (npm)
- MySQL
- Compute Engine
- Google Cloud Storage
- Google Cloud Build Pricing
- Google Cloud SQL

### Setup Cloud Infrastructure
Since both Cloud SQL and Cloud Run are on the Google Cloud Platform, it’s better to connect by private IP for minimum network latency. Enable the “Private IP” option if it’s not enabled yet. Keep note of the network and private IP address which will be used later.

For now, you only need to understand that Serverless VPC Access allows Cloud Functions, Cloud Run services, and App Engine apps to access resources in a VPC network using their private IPs.

Compute Engine default service account by default, should have the Cloud SQL Client role with permissions to connect to Cloud SQL.

create a Cloud Run service and connect it to our Cloud SQL instance with the VPC connector just created.

for more detail please follow this instruction [CLOUD_SERVICES](https://towardsdatascience.com/how-to-connect-to-gcp-cloud-sql-instances-in-cloud-run-servies-1e60a908e8f2)


## Testing the API
You can try to hit the API using the POSTMAN application.

### API Endpoints
| METHOD | ENDPOINT | FUNCTION |
| ------ | -------- | -------- |
| [GET] | `/` | to see if the API works |
| [POST] | `/register` | register user account |
| [POST] | `/login` | login user account |
| [GET] | `/api/auth/user` | show user data |
| [POST] | `/api/history` | save history from user |
| [GET] | `/history` | show user history |
| [POST] | `/api/uploadprofile` | upload profile picture from user |

### API Payloads
`/api/register`:
```json
{
    "name": " ",
    "email": " ",
    "password": " "
}
```

`/api/login`:
```json
{
    "email": " ",
    "password": " "
}
```