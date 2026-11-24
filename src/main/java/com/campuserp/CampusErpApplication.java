package com.campuserp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CampusERP — entry point.
 * Run with:  mvn spring-boot:run
 * Then open: http://localhost:8080  (log in as admin / admin123)
 */
@SpringBootApplication
public class CampusErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusErpApplication.class, args);
    }
}
