package com.dieti.dietiestatesbackend;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cache.annotation.EnableCaching;
 
@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing
@EnableCaching
public class DietiEstatesBackend {
    public static void main(String[] args) {
        // print env variable "SENDGRID_API_KEY"
        System.out.println("SENDGRID_API_KEY: " + System.getenv("SENDGRID_API_KEY"));
        SpringApplication.run(DietiEstatesBackend.class, args);
    }
}