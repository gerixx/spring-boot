API-first Spring Boot Example
-----------------------------

Implements an account management CRUD API including 
account-events that can be raised.

- Using OpenApi 3.0 for API declaration, see 
  "accounts-management/src/main/resources/accounts-management-api.yaml"
- Maven Wrapper 3.6.3
- Delegate implementations see package "com.smec.cc.accountsmanagement.api"

Build:
> mvnw clean install 

Start:
> mvnw spring-boot:run

Test:
> mvnw -Dtest=AccountsManagementApplicationTests test

http://localhost:8080/v1/account


Maven Wrapper
-------------
Use commands:
 mvnw is for Linux
 mvnw.cmd is for Windows

see also:

	https://medium.com/xebia-engineering/a-quick-introduction-to-maven-wrapper-f1d9dbb4ea5e#:~:text=jar%20file%20is%20used%20to,the%20Java%20class%20file%20MavenWrapperDownloader.

REST
----

POM: for Spring REST source code generation see used plugin artifact "openapi-generator-maven-plugin"

	https://reflectoring.io/spring-boot-openapi/
	https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin


Spring
------

Dependency versions overview: 

https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-dependency-versions.html#dependency-versions-properties


