# This is a Maven - Spring Boot project for searching places based on the address and radiusOfSearch as input to a GET REST API
# This project is not complete in terms of service implementation but it has the basic layered structure and my way of coding - standards.
# The test classes are not functional as the service implementation needs to be fixed.

#To run the code:
mvn clean install
mvn spring-boot:run
The service would be up on the embedded tomcat server with the following URL:

http://localhost:8080/v2/places/search/{address}/{radiusOfSearch}
