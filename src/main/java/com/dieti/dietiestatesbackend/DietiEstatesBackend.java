package com.dieti.dietiestatesbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DietiEstatesBackend {
    public static void main(String[] args) {
        SpringApplication.run(DietiEstatesBackend.class, args);
    }
}