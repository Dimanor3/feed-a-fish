package com.goia.feedafish;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/generate/fish")
    public Fish generateFishEndpoint() {
        Fish newFish = Fish.generateRandomFish();
        newFish.saveToDatabase(dataSource);
        return newFish;
    }
}
