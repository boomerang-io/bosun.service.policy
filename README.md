# Boomearng Bosun Service

**Description**

The Bosun service works with the Bosun UI and optionally the Repository service. It controls the CRUD on policy objects and the execution and validation of these poilicies as gates in your CICD pipelines.

**Prerequisites**

1. Java 1.11 
2. Springboot 2.1.8 
3. Maven
4. OpenPolicyAgent

## How to Run:

**Service**

1. `mvn package -Dversion.name=1.0`
2. `java -jar /target/services-boomerangci-1.0.jar`

**OpenPolicyAgent**

`docker run -p 8181:8181 openpolicyagent/opa run --server --log-level debug`

**Postman**

Included is a Postman collection that can be used to submit policies and test data into OPA

## OPA Policies

The policy rego documents are stored in the Helm chart

