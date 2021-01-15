#!/usr/bin/env bash

echo "Initializing DB"

# Create table
echo "Creating 'Coffee' table"
aws dynamodb create-table --cli-input-json file://./data/Coffee-Table.json --endpoint-url http://localhost:8000 --output text > /dev/null

# Insert Roasters
echo "Inserting sample data: Roasters"
aws dynamodb batch-write-item --request-items file://./data/Roasters.json --endpoint-url http://localhost:8000  --output text  > /dev/null

# Insert Offerings
echo "Inserting sample data: Offerings"
aws dynamodb batch-write-item --request-items file://./data/Offerings-CounterCulture.json --endpoint-url http://localhost:8000  --output text  > /dev/null
aws dynamodb batch-write-item --request-items file://./data/Offerings-LittleWolf.json --endpoint-url http://localhost:8000 --output text  > /dev/null
aws dynamodb batch-write-item --request-items file://./data/Offerings-GeorgeHowell.json --endpoint-url http://localhost:8000 --output text  > /dev/null

echo "Done"
