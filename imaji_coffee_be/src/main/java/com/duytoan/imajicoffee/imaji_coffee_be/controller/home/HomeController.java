package com.duytoan.imajicoffee.imaji_coffee_be.controller.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dump route for health balance check -> without it AWS will get error to initialize
 * @author duytoan
 * @since 10/2025
 */
@RestController
public class HomeController {
    @GetMapping("/")
    public String redirectToHealth() {
        return "App running. Check health at /actuator/health";
    }
}
