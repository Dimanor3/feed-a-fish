package com.goia.feedafish;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {
    
    @GetMapping("/getLatestFish")
    public Fish getLatestFish() {
        return Fish.generateRandomFish();
    }

    
}

