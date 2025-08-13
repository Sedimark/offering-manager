# Offering Manager

OfferingManager provides an API for managing offerings within a participant toolbox.
It also includes a self-listing access endpoint that allows users to interact with a catalog of offerings,
which are stored in a local, self-hosted database (planned for PostgreSQL integration,
at this moment just stored in a local variable with no persistence).

The API supports full CRUD (Create, Read, Update, Delete) operations for managing offerings,
each identified by a unique UUID. Additionally, it also provides access to the Self-Listing.

At this moment, offeringManager only provides a limited set of functionalities to play with offerings, although no
support for Offering formatting assessment or DLT interaction is included yet in this version (0.01).

---

## Table of Contents

- [Deployment](#deployment)
- [API Endpoints](#api-endpoints)
- [TODOs](#todos)

---

## Deployment

The OfferingManager application can be deployed using Docker with `docker-compose`.
It includes a `Dockerfile` for configuring and building the service image.

### Steps to Deploy

1. Clone the repository
2. Modify the env variables
    - CONTAINER_ENDPOINT: endpoint exposed of the API (e.g., http://0.0.0.0:8080).
    - HASH_ALGORITHM: Defines the hashing algorithm to be used for offering records.
      By default, this is set to SHA-256 and should not be modified.
    - EXTERNAL_ENDPOINT: public endpoint for the API
    - EXTERNAL_CONNECTOR_ENDPOINT: public connector endpoint (e.g. http://public-connector-endpoint:8185/api/public)
    - INTERNAL_CONNECTOR_ENDPOINT= private connector endpoint (e.g. http://private-connector-endpoint:8181)
    - INTERNAL_DLTBOOTH_ENDPOINT=(e.g. http://dlt-booth-private-endpoint:8085)
    - ENABLE_POSTGRES_PERSISTENCE: true or false
    - ENABLE_CONNECTOR_SERVICE. true or false
    - ENABLE_DLTBOOTH_SERVICE: true or false
    - POSTGRES_DB: offeringManager
    - POSTGRES_USER: offeringManager
    - POSTGRES_HOST: postgres-offering-manager
    - POSTGRES_PORT: 5432
    - POSTGRES_PASSWORD: change_this_password
3. Build and start the containerized (Docker) application:
   ```bash
   docker-compose up --build
    

## API Endpoints

The OfferingManager API provides the following CRUD endpoints for managing offerings.
API can be also found in the bruno and postman collections shared in this repo.

- **Create Offering**:  
  `POST /offerings`  
  **Description**: Registers a new offering (JSON-LD format).
  The ID is automatically generated, thus no @id should be included in the offering.
    - **Request Body**: JSON object representing the offering.
    - **Response**: `201 Created` on success, `400 Bad Request` on invalid input.
    - **Response Body**: Offering description + newly assigned @ID on success.

- **Retrieve Offering by ID**:
  `GET /offerings/{offeringId}`  
  **Description**: Retrieves a specific offering by its ID.
    - **Response**: `200 OK` with the offering description, `404 Not Found` if the offering does not exist.

- **Self-Listing endpoint: List All Offerings**:  
  `GET /offerings`
  **Description**: Retrieves a list of all available offerings.
    - **Optional headers for pagination: **: **page** for page number. **size** for number of offerings per page.
    - **Response**: `200 OK` with a JSON array of Offering URLs. If there is pagination, it returns the full Offering. Header **x-total-count** returns the number of existing offerings.

- **Update Offering by ID**:  
  `PUT /offerings/{offeringId}`  
  **Description**: Updates an existing offering. The current offering is removed and replaced with a new one (creating a
  new ID).
    - **Request Body**: JSON object with the updated offering.
    - **Response**: `201 Created` on success, or `404 Not Found` if the original offering ID does not exist.
    - **Response Body**: Offering description + newly assigned @ID on success.

- **Delete Offering by ID**:  
  `DELETE /offerings/{offeringId}`  
  **Description**: Deletes an offering from the system.
    - **Response**: `204 No Content` on success, `404 Not Found` if the offering does not exist.

## Notes

- **Security**:
  Limit certain endpoints to localhost access (i.e. CREATE/PUT, DELETE). Probably through Traefik.
