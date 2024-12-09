# PCOS Pal
Polycystic Ovary Syndrome (PCOS) is a collection of symptoms resulting from a disorder in the endocrine system and this condition is usually seen in reproductive-aged women. This syndrome may cause serious health problems.
Women who experienced this syndrome may experience infertility, impaired glucose tolerance, depression, Obstructive Sleep Apnea (OSA), increased risk of endometrial cancer and liver disease, diabetes, weight problems, and also possible to give birth to premature babies.
Many women, particularly in Indonesia, are unaware and they often ignore that they have symptoms or risk factors that increase their likelihood of experiencing PCOS. If not treated seriously, it’ll cause fatal consequences leading to death.
Therefore, we want to develop an easy-to-use application to help detect possibilities if they are at risk of experiencing PCOS. Although PCOS cannot be completely cured, its symptoms can be controlled and managed with the right treatment and a healthy lifestyle. We chose this case because we want to help reduce the women's death rate from disease.

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