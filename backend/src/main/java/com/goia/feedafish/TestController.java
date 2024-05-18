package com.goia.feedafish;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

    @GetMapping("/get/latest")
    public ResponseEntity<Fish> getLatestOrCreateFishEndpoint() {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish == null) {
                // If no latest fish exists, generate a new one
                latestFish = Fish.generateRandomFish();
                latestFish.saveToDatabase(dataSource);
                return ResponseEntity.status(HttpStatus.CREATED).body(latestFish);
            }
            return ResponseEntity.ok(latestFish);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/list/dead")
    public List<Fish> getAllDeadFishEndpoint() {
        return Fish.getAllDeadFish(dataSource);
    }
    @GetMapping("/feed/latest")
    public ResponseEntity<Fish> feedLatestFishEndpoint() {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            if (!latestFish.getAlive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            latestFish.feedFish(dataSource);
            return ResponseEntity.ok(latestFish);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Scheduled(fixedRate = 3600000) // Schedule this method to run every hour
    public void incrementHungerOfLatestFish() {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish != null && latestFish.getAlive()) {
                latestFish.setCurrentHungerLevel(latestFish.getCurrentHungerLevel() + 1);
                latestFish.saveToDatabase(dataSource);
            }
        } catch (Exception e) {
            System.out.println("Error incrementing hunger level of the latest fish: " + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><head><title>Fish Status</title></head><body>");
        htmlResponse.append("<h1>Fish Status Dashboard</h1>");

        try {
            List<Fish> allFish = Fish.getAllFish(dataSource);
            if (allFish.isEmpty()) {
                htmlResponse.append("<p>No fish data available.</p>");
            } else {
                htmlResponse.append("<table border='1'><tr><th>ID</th><th>Name</th><th>Alive</th><th>Weight</th><th>Min Weight</th><th>Max Weight</th><th>Current Hunger Level</th><th>Gain Weight Level</th><th>Lose Weight Level</th></tr>");
                for (Fish fish : allFish) {
                    htmlResponse.append("<tr>")
                            .append("<td>").append(fish.getId()).append("</td>")
                            .append("<td>").append(fish.getName()).append("</td>")
                            .append("<td>").append(fish.getAlive() ? "Yes" : "No").append("</td>")
                            .append("<td>").append(fish.getWeight()).append("</td>")
                            .append("<td>").append(fish.getMinWeight()).append("</td>")
                            .append("<td>").append(fish.getMaxWeight()).append("</td>")
                            .append("<td>").append(fish.getCurrentHungerLevel()).append("</td>")
                            .append("<td>").append(fish.getGainWeightHungerLevel()).append("</td>")
                            .append("<td>").append(fish.getLoseWeightHungerLevel()).append("</td>")
                            .append("</tr>");
                }
                htmlResponse.append("</table>");
            }
        } catch (Exception e) {
            htmlResponse.append("<p>Error retrieving fish data: ").append(e.getMessage()).append("</p>");
        }

        htmlResponse.append("</body></html>");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse.toString());
    }

}
