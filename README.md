# Offering Manager

OfferingManager provides an API for managing offerings within a participant toolbox. 
It also includes a self-listing access endpoint that allows users to interact with a catalog of offerings, 
which are stored in a local, self-hosted database (planned for PostgreSQL integration, 
at this moment just stored in a local variable with no persistence).

The API supports full CRUD (Create, Read, Update, Delete) operations for managing offerings, 
each identified by a unique UUID. Additionally, it also provides access to the Self-Listing.

At this moment, offeringManager only provides a limited set of functionalities to play with offerings, although no support for Offering formatting assessment or DLT interaction is included yet in this version (0.01).

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
   - BASE_URI: Represents the root path of the API (e.g., http://0.0.0.0:8080/offerings).
   - HASH_ALGORITHM: Defines the hashing algorithm to be used for offering records. 
   By default, this is set to SHA-256 and should not be modified.
3. Build and start the containerized (Docker) application:
   ```bash
   docker-compose up --build
    
## API Endpoints

The OfferingManager API provides the following CRUD endpoints for managing offerings:

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
    - **Response**: `200 OK` with a JSON array of Offering URLs.

- **Update Offering by ID**:  
  `PUT /offerings/{offeringId}`  
  **Description**: Updates an existing offering. The current offering is removed and replaced with a new one (creating a new ID).
    - **Request Body**: JSON object with the updated offering.
    - **Response**: `201 Created` on success, or `404 Not Found` if the original offering ID does not exist.
    - **Response Body**: Offering description + newly assigned @ID on success.

- **Delete Offering by ID**:  
  `DELETE /offerings/{offeringId}`  
  **Description**: Deletes an offering from the system.
    - **Response**: `204 No Content` on success, `404 Not Found` if the offering does not exist.

## TODOs

- **Offering Validation**:
Validate the Offering against the service expose by the "Interoperability Enabler" to ensure a proper formatting.

- **Database Integration**:
Persist the offering using a database (e.g. Postgres) compatible to the one used by the EDC Connector.

- **Security**:
Limit certain endpoints to localhost access (i.e. CREATE/PUT, DELETE). Probably through Traefik.

- **DLT Registration**: DONE
Register the offering reference and hash in the DLT.
