package com.goia.feedafish;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    // ...

    // @LocalServerPort
    private int port = 8081;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setupDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM fish");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='fish'");

        }
    }

    @AfterEach
    public void teardownDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM fish");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='fish'");
        }
    }

    @Test
    public void generateFishEndpointShouldReturnFish() {
        ResponseEntity<Fish> response = restTemplate.getForEntity("http://localhost:" + port + "/generate/fish", Fish.class);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void getLatestOrCreateFishEndpointShouldReturnFish() {
        ResponseEntity<Fish> response = restTemplate.getForEntity("http://localhost:" + port + "/get/latest", Fish.class);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void feedLatestFishEndpointShouldReturnUpdatedFish() {
        ResponseEntity<Fish> response = restTemplate.getForEntity("http://localhost:" + port + "/feed/latest", Fish.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAlive()).isTrue();
    }

    @Test
    public void statusEndpointShouldReturnStatusPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/status", String.class);
        assertThat(response.getBody()).contains("<title>Fish Status</title>");
    }
}
