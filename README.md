# Palindrome-api

A rest api developed using spring boot that accepts a username and text value and returns if the provided text value is a palindrome.

## How to Run

There are two runnable modes(controlled by spring profiles) that are available. For each mode a make bootstrap command has been provided. If make is not available on your system manual steps have also been provided.

### local

This basic approach is a primitive solution using a flat file datastore and an in memory cache.

**Prerequisites**

- java 17
- maven

**Make**

```bash
make bootstrap-local
```

**Manual**

```bash
	mvn clean package -f ./palindrome-api -DskipTests
    java -jar -Dspring.profiles.active=local ./palindrome-api/target/palindrome-api-0.0.1-SNAPSHOT.jar
```

### Scalable

This approach is designed to be a scalable solution that can utilize a shared datastore(MySql) and a shared cache(Redis). Below are instructuons to run the api in either a docker-compose stack or a local k8s cluster. It is also possible to run the jar directly with the scalable spring profile but you will need to provide your own database and redis instances.

**Prerequisites**

- java 17
- maven
- docker
- docker-compose
- kubectl & local k8s cluster

**Make**

_Docker Compose_

```bash
make bootstrap-docker
```

_k8s_

```bash
make bootstrap-k8s
#once the k8s service is up and running:
make k8s-port-forward
```

**Manual**

_Docker Compose_

```bash
mvn clean package -f ./palindrome-api -DskipTests
docker-compose up --build -d
```

_k8s_

```bash
mvn clean package -f ./palindrome-api -DskipTests
docker build -f ./palindrome-api/Dockerfile -t palindrome-api palindrome-api
kubectl apply -f deployment.yml
#once the k8s service is up and running:
kubectl port-forward  service/palindrome-api 8080:8080
```

## API Usage

```bash
curl --location --request POST 'localhost:8080/api/palindrome' \
--header 'Content-Type: application/json' \
--data-raw '{
    "word": "kayak",
    "userName": "username"
}'
```
