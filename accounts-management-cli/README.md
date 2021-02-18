
Account Manager CLI
-------------------

Build:
> mvnw clean install

Usage:
> java -jar target/accounts-management-cli-0.0.1-SNAPSHOT.jar

Options are printed with -h 

Generates Java REST API from yaml specification file in side project "accounts-management", 
see also "openapi-generator-maven-plugin" configuration in the pom file of this project.

Took info for the needed OpenApi 3 dependencies from:
https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-maven-plugin/examples/java-client.xml
