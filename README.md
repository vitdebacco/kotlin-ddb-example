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
- Run script to initialize DB and create sample data
  - `./data/init_db.sh`
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
- Create a Roaster
  - `curl 'http://localhost:8080/roasters' -X POST -d '{ "name": "New Roaster", "url": "https://www.newroaster.com", "status": "active" }' -H 'Accept: application/json' -H 'Content-Type: application/json' | jq`
- Update a Roaster
  - `curl 'http://localhost:8080/roasters/new-roaster' -X PUT -d '{ "name": "New Roaster Coffee", "url": "https://www.newroaster.coffee", "status": "test" }' -H 'Accept: application/json' -H 'Content-Type: application/json' | jq`
  - `curl 'http://localhost:8080/roasters/new-roaster' -X PUT -d '{ "status": "disabled" }' -H 'Accept: application/json' -H 'Content-Type: application/json' | jq`
- Delete a Roaster
  - `curl 'http://localhost:8080/roasters/new-roaster' -X DELETE -H 'Accept: application/json' -H 'Content-Type: application/json' | jq`

### About the Data Model
Coming soon!

### TODOs/Shortcomings to Document
- Document code
- Refactor/code cleanup
- More refactoring. Actually structure the code.
- Tests that don't rely on sample data 
- SO MANY BAD USES OF `!!` :(
- Complete `docker-compose.yml` to include app build
- Is there a way to model the full roaster into the offerings by origin GSI without embedding it?
- Offering model price is oversimplified. Also missing quantity.
- Lack of validation