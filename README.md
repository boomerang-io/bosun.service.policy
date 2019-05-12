# Boomearng Citadel Service

**Description**

The Citadel service works with the Citadel UI and the CI service. It controls the CRUD on ci_policies and the execution and validation of these poilicies as CI Gates.

**Prerequisites**

1. Java 1.8 
2. Springboot 
3. Maven
4. OpenPolicyAgent 

## How to Run:

**Service**

1. `mvn package -Dversion.name=1.0`
2. `java -jar /target/services-boomerangci-1.0.jar`

**OpenPolicyAgent**

`docker run -p 8181:8181 openpolicyagent/opa run --server --log-level debug`

## General Dependencies:

1. github.client.secret and github.client.id are hard coded in the application.properties file
2. urbancode.rest.user and urbancode.rest.password are hard coded in the application.properties file

