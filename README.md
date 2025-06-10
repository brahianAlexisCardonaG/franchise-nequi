🏪 Franchise API (Spring WebFlux - Clean Architecture)

This project is a reactive API for managing a franchise network, built with Spring WebFlux following the Clean Architecture pattern.
It allows you to manage franchises, branches, and products, and expose different operations through REST endpoints documented with OpenAPI/Swagger.


🖥️ Requirements to run locally

Download the franchise repository

install IDE for java.(run application, also executes dependencies) 
In most cases it is used IntelliJ IDEA

JDK 17+
It is important to have this tool to run the project locally.

Gradle 8+
Install all dependencies for the application to work correctly

port: 8085
la aplicacion corre de manera local en el puerto 8085 en las rutas
http://localhost:8085
http://localhost:8085/webjars/swagger-ui/index.html

Docker (optional, to run in a container)
You can install Docker Desktop and run the image, saving you the installation steps for different tools.
Docker Hub: https://hub.docker.com/repository/docker/brahiancard/image-franchise/general

Access to a Supabase instance (configured as in application.yml)


📚 Available Endpoints

Franchise
POST /api/v1/franchise → Create franchise
{
  "name": "macdonals" //name of franchise
}

GET /api/v1/franchise → Get franchise
//sending the franchise ID as a parameter, we can obtain the branches and the products with the largest stock associated with those branches.

PATCH /api/v1/franchise/name → Update name of franchise (plus)
//
{
  "id": 1,  //id of the feanchise to change name
  "name": "Macdonal" //new name of franchise
}


Branch
POST /api/v1/branch → Add Branch
{
  "name": "Macdonals cartago", //name of branch
  "franchiseId": 0 // id of the associated franchise
}

PUT /api/v1/branch/name → Actualizar nombre de sucursal (plus)
{
  "id": 1, //id of the branch to change name
  "name": "Macdonal pereira" //new name of branch
}

Product
POST /api/v1/product → Save product
{
  "name": "macflurry", //name of product
  "stock": 10, //number of existing products
  "branchId": 1 // id of the associated branch
}

DELETE /api/v1/product → Delete Product of Branch producto
//sending the product ID as a parameter, we can disassociate the product and the products with associated branches.

PUT /api/v1/product/stock → Update stock of product
{
  "stock": 15, //quantity of stock to modify
  "id": 1 // id of product to stock update 
}

PUT /api/v1/product/name → update name of product (plus)
{
  "id": 1,
  "name": "McFlurry Ch"
}

🔗 Repositorios
Imagen Docker published in Docker Hub: https://hub.docker.com/repository/docker/brahiancard/image-franchise/general
