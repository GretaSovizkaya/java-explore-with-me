package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"main", "client"})
public class EWMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(EWMainApplication.class, args);
    }
}
