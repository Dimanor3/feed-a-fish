package com.goia.feedafish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

public class Fish {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private Integer parentFishId;
    private String base64Image;
    private String imagePath;
    private String json;

    public Fish() {
    }

    public Fish(Long id, String name, Timestamp createdAt, Integer parentFishId, String base64Image, String imagePath, String json) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.parentFishId = parentFishId;
        this.base64Image = base64Image;
        this.imagePath = imagePath;
        this.json = json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getParentFishId() {
        return parentFishId;
    }

    public void setParentFishId(Integer parentFishId) {
        this.parentFishId = parentFishId;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void saveToDatabase(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO fish (name, created_at, parent_fish_id, base64_image, image_path, json) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.name);
                pstmt.setTimestamp(2, this.createdAt);
                if (this.parentFishId == null) {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(3, this.parentFishId);
                }
                pstmt.setString(4, this.base64Image);
                pstmt.setString(5, this.imagePath);
                pstmt.setString(6, this.json);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error saving fish to database: " + e.getMessage());
        }
    }

    public static Fish getLatestFish(DataSource dataSource) {
        Fish fish = null;
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM fish ORDER BY created_at DESC LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    fish = new Fish();
                    fish.setId(rs.getLong("id"));
                    fish.setName(rs.getString("name"));
                    fish.setCreatedAt(rs.getTimestamp("created_at"));
                    fish.setParentFishId((Integer) rs.getObject("parent_fish_id"));
                    fish.setBase64Image(rs.getString("base64_image"));
                    fish.setImagePath(rs.getString("image_path"));
                    fish.setJson(rs.getString("json"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving latest fish from database: " + e.getMessage());
        }
        return fish;
    }

    public static Fish generateRandomFish() {
        Fish fish = new Fish();
        Random random = new Random();

        // Generate random name
        String[] names = {"Goldie", "Nemo", "Dory", "Bubbles", "Splash", "Finny"};
        fish.setName(names[random.nextInt(names.length)]);

        // Set current timestamp as created_at
        fish.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Randomly assign a parent fish ID or null
        fish.setParentFishId(random.nextBoolean() ? null : random.nextInt(100) + 1);

        // Generate random base64 image string (mock)
        fish.setBase64Image("data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[20]));

        // Generate random image path
        fish.setImagePath("/images/" + UUID.randomUUID().toString() + ".png");

        // Generate random JSON data
        String json = String.format("{\"mood\":\"%s\",\"age\":%d}", 
            random.nextBoolean() ? "happy" : "sad", random.nextInt(10) + 1);
        fish.setJson(json);

        return fish;
    }
}