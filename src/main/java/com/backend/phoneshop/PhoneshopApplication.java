package com.backend.phoneshop;

import com.backend.phoneshop.config.security.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class PhoneshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneshopApplication.class, args);
    }

}
