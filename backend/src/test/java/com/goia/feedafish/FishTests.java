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
                "imagePath", "{}", true, 1.0, 0.5, 2.0, 5, 8, 3);
        fish.saveToDatabase(dataSource);

        Fish latestFish = Fish.getLatestFish(dataSource);
        assertNotNull(latestFish, "Latest fish should not be null after saving a new fish.");
        assertEquals("Goldfish", latestFish.getName(), "The name of the latest fish should match the one just saved.");
        assertEquals("base64Image", latestFish.getBase64Image(),
                "The base64 image of the latest fish should match the one just saved.");
        assertEquals("imagePath", latestFish.getImagePath(),
                "The image path of the latest fish should match the one just saved.");
        assertEquals("{}", latestFish.getJson(), "The JSON of the latest fish should match the one just saved.");
        assertTrue(latestFish.getAlive(), "The fish should be alive.");
        assertEquals(1.0, latestFish.getWeight(), "The weight of the fish should be 1.0.");
        assertEquals(0.5, latestFish.getMinWeight(), "The minimum weight of the fish should be 0.5.");
        assertEquals(2.0, latestFish.getMaxWeight(), "The maximum weight of the fish should be 2.0.");
        assertEquals(5, latestFish.getCurrentHungerLevel(), "The current hunger level of the fish should be 5.");
        assertEquals(8, latestFish.getGainWeightHungerLevel(), "The gain weight hunger level of the fish should be 8.");
        assertEquals(3, latestFish.getLoseWeightHungerLevel(), "The lose weight hunger level of the fish should be 3.");
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
        assertTrue(randomFish.getAlive(), "Random fish should be alive.");
        assertNotNull(randomFish.getWeight(), "Random fish should have a weight.");
        assertNotNull(randomFish.getMinWeight(), "Random fish should have a minimum weight.");
        assertNotNull(randomFish.getMaxWeight(), "Random fish should have a maximum weight.");
        assertNotNull(randomFish.getCurrentHungerLevel(), "Random fish should have a current hunger level.");
        assertNotNull(randomFish.getGainWeightHungerLevel(), "Random fish should have a gain weight hunger level.");
        assertNotNull(randomFish.getLoseWeightHungerLevel(), "Random fish should have a lose weight hunger level.");

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
