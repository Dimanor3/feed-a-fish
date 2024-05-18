package com.goia.feedafish;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FishTests {

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setupDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM fish");
        }
    }

    @AfterEach
    public void tearDownDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM fish");
        }
    }

    @Test
    public void testSaveToDatabase() {
        Fish fish = new Fish(1L, "Goldfish", new Timestamp(System.currentTimeMillis()), null, "base64Image",
                "imagePath", "{}");
        fish.saveToDatabase(dataSource);

        Fish latestFish = Fish.getLatestFish(dataSource);
        assertNotNull(latestFish, "Latest fish should not be null after saving a new fish.");
        assertEquals("Goldfish", latestFish.getName(), "The name of the latest fish should match the one just saved.");
        assertEquals("base64Image", latestFish.getBase64Image(),
                "The base64 image of the latest fish should match the one just saved.");
        assertEquals("imagePath", latestFish.getImagePath(),
                "The image path of the latest fish should match the one just saved.");
        assertEquals("{}", latestFish.getJson(), "The JSON of the latest fish should match the one just saved.");
    }

    @Test
    public void testGenerateRandomFish() {
        Fish randomFish = Fish.generateRandomFish();
        assertNotNull(randomFish, "Random fish should not be null.");
        assertNotNull(randomFish.getName(), "Random fish should have a name.");
        assertNotNull(randomFish.getCreatedAt(), "Random fish should have a creation timestamp.");
        assertNotNull(randomFish.getBase64Image(), "Random fish should have a base64 image.");
        assertNotNull(randomFish.getImagePath(), "Random fish should have an image path.");
        assertNotNull(randomFish.getJson(), "Random fish should have JSON data.");

        // Validate the structure of the base64 image string
        assertTrue(randomFish.getBase64Image().startsWith("data:image/png;base64,"),
                "Base64 image should start with the correct prefix.");

        // Validate the structure of the image path
        assertTrue(randomFish.getImagePath().startsWith("/images/"),
                "Image path should start with '/images/'.");

        // Validate JSON structure
        assertDoesNotThrow(() -> new JSONObject(randomFish.getJson()),
                "JSON data should be in a valid JSON format.");
    }
    
    
    

}
