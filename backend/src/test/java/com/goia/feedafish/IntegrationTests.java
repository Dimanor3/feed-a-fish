package com.goia.feedafish;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/generate/fish",
                HttpMethod.POST, entity, String.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"alive\"");
    }

    @Test
    public void getLatestOrCreateFishEndpointShouldReturnFish() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/get/latest",
                HttpMethod.GET, entity, String.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"alive\":");
    }

    @Test
    public void feedLatestFishEndpointShouldReturnUpdatedFish() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/feed/latest",
                HttpMethod.GET, entity, String.class);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void statusEndpointShouldReturnStatusPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/status", HttpMethod.GET,
                entity, String.class);
        assertThat(response.getBody()).contains("<title>Fish Status</title>");
    }
}
