package com.duytoan.imajicoffee.imaji_coffee_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableCaching
public class ImajiCoffeeBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImajiCoffeeBeApplication.class, args);
    }

}
