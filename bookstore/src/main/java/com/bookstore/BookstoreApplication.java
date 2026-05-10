package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Online Bookstore is running!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
