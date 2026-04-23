package com.example.insightflowserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private CorsProperties corsProperties;

    @CrossOrigin(origins = "${cors.allowed-origins}")
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from InsightFlow Server!";
    }
}
