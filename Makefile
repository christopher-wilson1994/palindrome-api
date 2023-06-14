
####BUILD####
build-service: 
	mvn clean package -f ./palindrome-api -DskipTests


build-image:
	docker build -f ./palindrome-api/Dockerfile -t palindrome-api palindrome-api


####RUN####
run-docker: 
	docker-compose up --build -d
run: 
	java -jar -Dspring.profiles.active=local ./palindrome-api/target/palindrome-api-0.0.1-SNAPSHOT.jar 

run-k8s:
	kubectl apply -f deployment.yml

k8s-port-forward:
	kubectl port-forward  service/palindrome-api 8080:8080


####BOOTSTRAP####
bootstrap-docker: build-service run-docker
bootstrap-local: build-service run
bootstrap-k8s: build-service build-image run-k8s

