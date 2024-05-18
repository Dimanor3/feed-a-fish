package com.goia.feedafish;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
public class TestController {
    private final ResourceHttpRequestHandler resourceHandler;

    public TestController() {
        this.resourceHandler = new ResourceHttpRequestHandler();
        Resource location = new ClassPathResource("static/");
        this.resourceHandler.setLocations(List.of(location));
    }

    @GetMapping("/getLatestFish")
    public Fish getLatestFish() {
        return Fish.generateRandomFish();
    }

    
}

