package com.goia.feedafish;

import org.junit.jupiter.api.BeforeEach;
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
    
    
}
