package com.backend.phoneshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PhoneshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneshopApplication.class, args);
    }

}
