package com.goia.feedafish;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    // ...

    // @LocalServerPort
    private int port = 8081;

    // ...

    @Test
    public void generateFishEndpointShouldReturnFish() {
        ResponseEntity<Fish> response = restTemplate.getForEntity("http://localhost:" + port + "/generate/fish", Fish.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isNotNull();
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
        assertThat(response.getBody()).contains("<h1>Fish Status Dashboard</h1>");
    }
}
