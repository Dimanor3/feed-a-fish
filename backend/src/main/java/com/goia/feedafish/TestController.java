package com.goia.feedafish;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestController {
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping(value = "/generate/fish", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateFishEndpoint(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Boolean alive,
                                     @RequestParam(required = false) Double weight,
                                     @RequestParam(required = false) Double minWeight,
                                     @RequestParam(required = false) Double maxWeight,
                                     @RequestParam(required = false) Integer currentHungerLevel,
                                     @RequestParam(required = false) Integer gainWeightHungerLevel,
                                     @RequestParam(required = false) Integer loseWeightHungerLevel,
                                     @RequestParam(required = false) String base64Image,
                                     @RequestParam(required = false) String imagePath,
                                     @RequestParam(required = false) String mood,
                                     @RequestParam(required = false) Integer age) {
        Fish newFish = Fish.generateRandomFish();

        if (name != null) newFish.setName(name);
        if (alive != null) newFish.setAlive(alive);
        if (weight != null) newFish.setWeight(weight);
        if (minWeight != null) newFish.setMinWeight(minWeight);
        if (maxWeight != null) newFish.setMaxWeight(maxWeight);
        if (currentHungerLevel != null) newFish.setCurrentHungerLevel(currentHungerLevel);
        if (gainWeightHungerLevel != null) newFish.setGainWeightHungerLevel(gainWeightHungerLevel);
        if (loseWeightHungerLevel != null) newFish.setLoseWeightHungerLevel(loseWeightHungerLevel);
        if (base64Image != null) newFish.setBase64Image(base64Image);
        if (imagePath != null) newFish.setImagePath(imagePath);
        if (mood != null) newFish.setMood(mood);
        if (age != null) newFish.setAge(age);

        newFish.setParentFishId(0);


        newFish.saveToDatabase(dataSource);
        return ResponseEntity.ok(newFish.toJson());
    }

    @GetMapping(value = "/get/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLatestOrCreateFishEndpoint(ZoneId zoneId) {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish == null) {
                LocalDateTime curTime = LocalDateTime.now(zoneId);
                System.out.println(curTime.getHour() + " " + curTime.getMinute());
//                if (curTime.getHour() == 11 && curTime.getMinute() == 11) {
                if (true) {
                    // If no latest fish exists, generate a new one
                    latestFish = Fish.generateRandomFish();
                    latestFish.saveToDatabase(dataSource);
                    return ResponseEntity.status(HttpStatus.CREATED).body(latestFish.toJson());
                } else {
                    return ResponseEntity.status(HttpStatus.CREATED).body("dead");
                }
            }
            return ResponseEntity.ok(latestFish != null ? latestFish.toJson() : "{}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/get/dead", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllDeadFishEndpoint() {
        try {
            List<Fish> deadFishList = Fish.getAllDeadFish(dataSource);
            return ResponseEntity.ok(Fish.listToJson(deadFishList).toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/feed/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> feedLatestFishEndpoint() {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"No latest fish found\"}");
            }
            if (!latestFish.getAlive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Latest fish is not alive\"}");
            }
            if (!latestFish.feedFish(dataSource)) {
                return ResponseEntity.status(HttpStatus.CREATED).body("dead");
            }

            return ResponseEntity.ok(latestFish.toJson());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/ip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getClientIp(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        return ResponseEntity.ok("{\"ip\": \"" + clientIp + "\"}");
    }

    @GetMapping(value = "/server-ip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getServerIp(HttpServletRequest request) {
        String serverIp = request.getLocalAddr();
        return ResponseEntity.ok("{\"server_ip\": \"" + serverIp + "\"}");
    }

    @Scheduled(fixedRate = 3600000) // Schedule this method to run every hour
    public void incrementHungerOfLatestFish() {
        try {
            Fish latestFish = Fish.getLatestFish(dataSource);
            if (latestFish != null && latestFish.getAlive()) {
                int previousHungerLevel = latestFish.getCurrentHungerLevel();
//                latestFish.setCurrentHungerLevel(previousHungerLevel + 1);
                latestFish.makeHungrier(dataSource);
                latestFish.updateInDatabase(dataSource);
                System.out.println("Hunger level incremented for fish ID: " + latestFish.getId() + 
                                   ", Name: " + latestFish.getName() + 
                                   ", Previous Hunger Level: " + previousHungerLevel + 
                                   ", Current Hunger Level: " + latestFish.getCurrentHungerLevel());
            }
        } catch (Exception e) {
            System.out.println("Error incrementing hunger level of the latest fish: " + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><head><title>Fish Status</title>");
        htmlResponse.append("<link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">");
        htmlResponse.append("<style>");
        htmlResponse.append("body { background: #00c6ff; background: -webkit-linear-gradient(to right, #0072ff, #00c6ff); background: linear-gradient(to right, #0072ff, #00c6ff); }");
        htmlResponse.append(".table-container { background-color: rgba(255, 255, 255, 0.8); padding: 20px; border-radius: 10px; }");
        htmlResponse.append(".wave { position: absolute; bottom: 0; width: 100%; height: 100px; background: url('https://example.com/wave.svg'); background-size: cover; animation: wave-animation 10s infinite linear; }");
        htmlResponse.append("@keyframes wave-animation { 0% { transform: translateX(0); } 100% { transform: translateX(-100%); } }");
        htmlResponse.append("</style>");
        htmlResponse.append("</head><body class=\"flex items-center justify-center min-h-screen\">");
        htmlResponse.append("<div class=\"wave\"></div>");
        htmlResponse.append("<div class=\"table-container\">");
        htmlResponse.append("<h1 class=\"text-3xl font-bold mb-4\">Fish Status Dashboard</h1>");

        try {
            List<Fish> allFish = Fish.getAllFish(dataSource);
            if (allFish.isEmpty()) {
                htmlResponse.append("<p class=\"text-red-500\">No fish data available.</p>");
            } else {
                htmlResponse.append("<table class=\"min-w-full bg-white border border-gray-300\"><thead><tr>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">ID</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Name</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Alive</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Weight</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Min Weight</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Max Weight</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Current Hunger Level</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Gain Weight Level</th>");
                htmlResponse.append("<th class=\"py-2 px-4 border-b\">Lose Weight Level</th>");
                htmlResponse.append("</tr></thead><tbody>");
                for (Fish fish : allFish) {
                    htmlResponse.append("<tr>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getId()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getName()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getAlive() ? "Yes" : "No").append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getWeight()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getMinWeight()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getMaxWeight()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getCurrentHungerLevel()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getGainWeightHungerLevel()).append("</td>")
                            .append("<td class=\"py-2 px-4 border-b\">").append(fish.getLoseWeightHungerLevel()).append("</td>")
                            .append("</tr>");
                }
                htmlResponse.append("</tbody></table>");
            }
        } catch (Exception e) {
            htmlResponse.append("<p class=\"text-red-500\">Error retrieving fish data: ").append(e.getMessage()).append("</p>");
        }

        htmlResponse.append("</div></body></html>");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse.toString());
    }
}
