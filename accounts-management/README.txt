Build:
> mvnw clean install 

Start:
> mvnw spring-boot:run

Test:
> mvnw -Dtest=AccountsManagementApplicationTests test

http://localhost:8080/v1/account


Using Maven Wrapper:
--------------------
use commands:
 mvnw is used for Linux
 mvnw.cmd is used for Windows

see also:
	https://medium.com/xebia-engineering/a-quick-introduction-to-maven-wrapper-f1d9dbb4ea5e#:~:text=jar%20file%20is%20used%20to,the%20Java%20class%20file%20MavenWrapperDownloader.

REST
----

Using OpenApi 3 with API-firest approach, see .yaml file:
${project.basedir}/src/main/resources/accounts-management-api.yaml

POM: for Spring REST source code see used plugin artifact "openapi-generator-maven-plugin"
	https://reflectoring.io/spring-boot-openapi/
	https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin

IntelliJ Cheat Sheet
--------------------

Ctrl+N : finds a class by name.
Ctrl+Shift+N : finds any file or directory by name (supports CamelCase and snake_case). note. ...
Ctrl+Alt+Shift+N : finds a symbol. ...
Ctrl+Shift+A : finds an action by name.
