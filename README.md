# Store Application
The Store application keeps track of customers and orders in a database.

# Tech Stack
- Java 17
- Spring Boot 3.4.2
- PostgreSQL 16
- Redis
- liquibase
- mapstruct

# Assumptions
* A product can be added to the order only once
* A product will have a unique description to avoid duplicates

# Prerequisites
* This service assumes the presence of a postgresql 16.2 database server running on localhost:5433 (note the non-standard port)
* It assumes a username and password `admin:admin` can be used.
* It assumes there's already a database called `store`

You can start the PostgreSQL instance like this:
```shell
docker run -d \
  --name postgres \
  --restart always \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=store \
  -v postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:16.2 \
  postgres -c wal_level=logical
```

# Running the application
You should be able to run the service using
```shell
./gradlew bootRun
```

# Data model
* An order has an ID, a description, and is associated with the customer which made the order.
* A customer has an ID, a name, and 0 or more orders.
* A product has an ID and a description
* A single order contains 1 or more products
* An order will have only a single product of each

# Completed Tasks
* Added a new endpoint to fetch specific order by id and implemented caching
* Updated customer endpoint to query by customer name and implemented offset based pagination
* Added cursor based pagination for order, product list endpoints to improve performance 
* Added caching for individual resource endpoints
* Added new endpoint to create & fetch products
* Added a new endpoint to add a product to an existing order
* Updated the orders endpoint to return product ids
* Added a Dockerfile/docker-compose

# API 
Latest OpenAPI.yaml can be found in the code
   * GET /order
   * GET /order/{id}
   * POST /order
   * POST /order/{id}/products/{productId}

   * GET /customer?query={name}
   * POST /customer

   * GET /product
   * GET /product/{id}
   * POST /product

