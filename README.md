spring-boot
Technical:
Spring Boot 3.1.0
Java 17
Thymeleaf
Bootstrap v.4.3.1
Setup with Intellij IDE
Create project from Initializr: File > New > project > Spring Initializr
Add lib repository into pom.xml
Add folders
Source root: src/main/java
View: src/main/resources
Static: src/main/resource/static
Create database with name "demo" as configuration in application.properties
Run sql script to create table doc/data.sql
Implement a Feature
Create mapping domain class and place in package com.nnk.springboot.domain
Create repository class and place in package com.nnk.springboot.repositories
Create controller class and place in package com.nnk.springboot.controllers
Security
Create user service to load user from database and place in package com.nnk.springboot.services
Add configuration class and place in package com.nnk.springboot.config
