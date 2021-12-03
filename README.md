# ISSUE TRACKER


---

### Assumptions taken as per understanding of the specs.

>- Only the stories with STATUS=ESTIMATED are included in the plan regardless of their estimated point value.
>- Each week the stories included will be less than equal to the Number of developers in the systems mutliplied by a factor of average developer capacity which is 10, but it is configurable via application.properties and Story.estimatedValue in the entities.

----
### Tested Environment / Requirements
#### JAVA SDK 11
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)

#### MAVEN 3
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: C:\IT_CodeRepo\Installed-Soft\maven3\bin\..
Java version: 11.0.2, vendor: Oracle Corporation, runtime: C:\IT_CodeRepo\Installed-Soft\Java\jdk-11.0.2
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"

### MYSQL/MariaDB 15 (Optional, only required for mysql profile)
mysql  Ver 15.1 Distrib 10.4.21-MariaDB, for Win64 (AMD64), source revision 4902b0fdc91cc6dc169dd2322daf966a2eeafdd8

---
## How to run?
### Change to where zip file is extracted
`cd issuetracker`

## RUN
> ### `mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql`

OR

> ### `mvn clean spring-boot:run -Dspring-boot.run.profiles=hsqldb`

OR 

> ### `mvn clean spring-boot:run -Dspring-boot.run.profiles=nodata`

### COMPILE
`mvn clean compile`

### PACKAGE
`mvn clean package`

### Run directly from packaged jar
#### Set Environment Variable for profile.
`set spring_profiles_active=hsqldb`

OR

`set spring_profiles_active=mysql`
#### Run through jar
`java -jar target\issuetracker-1.0.jar`

---
>## SAMPLE DATA
- ### Application is bunlded with sample data for HSQLDB and MySQL
- ### To start the application with no data run in *nodata* profile mode i.e.
###`mvn clean spring-boot:run -Dspring-boot.run.profiles=nodata`

OR

`set spring_profiles_active=nodata`
#### Run through jar
`java -jar target\issuetracker-1.0.jar`

---
>## WEB USER INTERFACE
>> ### After running the application open http://localhost:8080/

---
>## REST REQUESTS

> ### DEVELOPERS
#### CREATE
curl --verbose --location --request POST 'http://192.168.43.124:8080/api/v1/rest/developers' --header 'Content-Type: application/json' --data-raw '{
"name":"Tom"
}' | jq

#### READ
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/developers/1' | jq

#### READ ALL 
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/developers' | jq

#### UPDATE
curl --verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/developers/1' --header 'Content-Type: application/json' --data-raw '{
"name":"Zen"
}' | jq

#### DELETE
curl --verbose --location --request DELETE 'http://192.168.43.124:8080/api/v1/rest/developers/1' | jq

---

> ### STORIES
#### CREATE
curl --verbose --location --request POST 'http://192.168.43.124:8080/api/v1/rest/stories' --header 'Content-Type: application/json' --data-raw '{
"title":"S200",
"status": "NEW",
"estimatedPointValue": 0
}' | jq

#### READ
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/stories/1' | jq

#### READ ALL
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/stories' | jq

#### UPDATE
curl --verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/stories/35' --header 'Content-Type: application/json' --data-raw '{
"title": "S200",
"description": "Story 200",
"developer": {"id": 2},
"status": "ESTIMATED",
"estimatedPointValue": 9
}' | jq

#### DELETE
curl --verbose --location --request DELETE 'http://192.168.43.124:8080/api/v1/rest/stories/35' | jq

---

> ### BUGS
#### CREATE
curl --verbose --location --request POST 'http://192.168.43.124:8080/api/v1/rest/bugs' --header 'Content-Type: application/json' --data-raw '{
"title":"B3",
"description": "Bug 3",
"status": "NEW",
"priority": "MINOR"
}' | jq

#### READ
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/bugs/31' | jq

#### READ ALL
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/bugs' | jq

#### UPDATE
curl --verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/bugs/36' --header 'Content-Type: application/json' --data-raw '{
"title":"B03",
"description": "Bug 03",
"status": "VERIFIED",
"priority": "MAJOR",
"developer": {"id": 2}
}' | jq

#### DELETE
curl --verbose --location --request DELETE 'http://192.168.43.124:8080/api/v1/rest/bugs/36' | jq


---

> ### PLANS
#### GET - Generates and returns the plan without doing any assigment to stories
curl --verbose --location --request GET 'http://192.168.43.124:8080/api/v1/rest/plans' | jq

### PUT - Generates and returns the plan also does the assignment based on the path value(false/true) false is default
> PUT - Without stories assignment

curl --verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/plans' | jq

> PUT - Without stories assignment

curl -verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/plans/false' | jq

> ####PUT - With stories assignment

curl -verbose --location --request PUT 'http://192.168.43.124:8080/api/v1/rest/plans/true' | jq

---
### What's there?
- Planning Algorithm time complexity is LINEAR O(n); it is optimal and fair for unassigned stories.
- Rest based CRUD services. 
- Web User Interface for CRUD operations.
- Planning services for getting the plan as well as getting and auto assigning stories. 

---
### What's missing? / TODO List
- Although, the planning algorithm picks/assigns a developer optimally as well as fairly in case of unassigned stories, it could not be fair in few/rare cases of assigned stories.
- Rest requests for bulk operations.
- Indexes to speed up querying of data.
- Integration and Unit tests.

---

### Technical Choices
- Spring Boot is used as an application framework. It takes an opinionated approach to use Spring platform and helps build production grade application in faster and efficient way.
- Lombok is used to get rid of writing the boilerplate getters, setters, toStrings, constructors specifically for the entities.
