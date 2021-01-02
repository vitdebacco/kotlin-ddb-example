# Kotlin DynamoDB Example
This app provides a basic proof of concept for working with DynamoDB using Kotlin.

### Technologies
- Kotlin
- DynamoDB
- [Jooby](https://www.jooby.org/)
- Netty

### Dependencies & Useful Tools
- AWS CLI for sample data initialization
- NoSQL Workbench for visualization of the model and sample data
- jq for pretty JSON

## Getting Started

### Running Locally
- Start DynamoDB local via Docker 
  - `docker run -p 8000:8000 amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -inMemory`
    - be sure to include the `sharedDb` flag to easily interact with the sample data using the sample app, AWS cli, and NoSQL Workbench
    - including the `inMemory` flag ensures that the DB will not be persisted to disk. Useful for tests and tweaking the model, but 
      may not be ideal for playing with the app itself.
  - Or use the provided `docker-compose.yml` (currently only contains DynamoDB dependency)
- Create `Coffee` Table (You may need to configure AWS client)
  - `aws dynamodb create-table --cli-input-json file://./src/main/resources/Coffee-Table.json --endpoint-url http://localhost:8000`
- Create Sample Data
  - Roasters
    - `aws dynamodb batch-write-item --request-items file://./src/main/resources/Roasters.json --endpoint-url http://localhost:8000`
  - Offerings
    - `aws dynamodb batch-write-item --request-items file://./src/main/resources/Offerings-CounterCulture.json --endpoint-url http://localhost:8000`
    - `aws dynamodb batch-write-item --request-items file://./src/main/resources/Offerings-LittleWolf.json --endpoint-url http://localhost:8000`
    - `aws dynamodb batch-write-item --request-items file://./src/main/resources/Offerings-GeorgeHowell.json --endpoint-url http://localhost:8000`
- Start the App
  - `./gradlew joobyRun`
- Query the API!
    
### Sample Requests
- Retrieve a Roaster by ID
  - `curl 'http://localhost:8080/roasters/counter-culture-coffee' -H 'accept: application/json' | jq`
- Retrieve Offerings by Roaster ID
  - `curl 'http://localhost:8080/roasters/little-wolf-coffee/offerings' -H 'accept: application/json' | jq`
- Retrieve Offerings by Origin, Roaster
  - `curl 'http://localhost:8080/offerings?origin_name=ecuador' -H 'accept: application/json' | jq`
  - `curl 'http://localhost:8080/offerings?origin_name=ecuador&roaster_id=counter-culture-coffee' -H 'accept: application/json' | jq`

### About the Data Model
Coming soon!

### TODOs
- Document code
- Refactor/code cleanup
- More refactoring. Actually structure the code.
- SO MANY BAD USES OF `!!` :(
- Complete `docker-compose.yml` to include app build
- Is there a way to model the full roaster into the offerings by origin GSI without embedding it?
- Offering model price is oversimplified. Also missing quantity.