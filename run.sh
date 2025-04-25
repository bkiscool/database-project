#!/bin/bash -e

./reset_database.sh

./mvnw spring-boot:run

#./mvnw clean package
#./jdk-21.0.7/bin/javaw.exe -jar ./target/project-0.0.1-SNAPSHOT.jar

#echo "Done"
#read
