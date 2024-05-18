package com.goia.feedafish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import org.json.JSONObject;

public class Fish {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private Integer parentFishId;
    private String base64Image;
    private String imagePath;
    private String json;
    private Boolean alive;
    private Double weight;
    private Double minWeight;
    private Double maxWeight;
    private Integer currentHungerLevel;
    private Integer gainWeightHungerLevel;
    private Integer loseWeightHungerLevel;

    public Fish() {
    }

    public Fish(Long id, String name, Timestamp createdAt, Integer parentFishId, String base64Image, String imagePath, String json, Boolean alive, Double weight, Double minWeight, Double maxWeight, Integer currentHungerLevel, Integer gainWeightHungerLevel, Integer loseWeightHungerLevel) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.parentFishId = parentFishId;
        this.base64Image = base64Image;
        this.imagePath = imagePath;
        this.json = json;
        this.alive = alive;
        this.weight = weight;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.currentHungerLevel = currentHungerLevel;
        this.gainWeightHungerLevel = gainWeightHungerLevel;
        this.loseWeightHungerLevel = loseWeightHungerLevel;
    }

    // Getters and setters for all fields
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

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(Double minWeight) {
        this.minWeight = minWeight;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Integer getCurrentHungerLevel() {
        return currentHungerLevel;
    }

    public void setCurrentHungerLevel(Integer currentHungerLevel) {
        this.currentHungerLevel = currentHungerLevel;
    }

    public Integer getGainWeightHungerLevel() {
        return gainWeightHungerLevel;
    }

    public void setGainWeightHungerLevel(Integer gainWeightHungerLevel) {
        this.gainWeightHungerLevel = gainWeightHungerLevel;
    }

    public Integer getLoseWeightHungerLevel() {
        return loseWeightHungerLevel;
    }

    public void setLoseWeightHungerLevel(Integer loseWeightHungerLevel) {
        this.loseWeightHungerLevel = loseWeightHungerLevel;
    }                                                                                                                                                                                                                                                                                                  

    public void saveToDatabase(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO fish (name, created_at, parent_fish_id, base64_image, image_path, json, alive, weight, min_weight, max_weight, current_hunger_level, gain_weight_hunger_level, lose_weight_hunger_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                pstmt.setBoolean(7, this.alive);
                pstmt.setDouble(8, this.weight);
                pstmt.setDouble(9, this.minWeight);
                pstmt.setDouble(10, this.maxWeight);
                pstmt.setInt(11, this.currentHungerLevel);
                pstmt.setInt(12, this.gainWeightHungerLevel);
                pstmt.setInt(13, this.loseWeightHungerLevel);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error saving fish to database: " + e.getMessage());
        }
    }

    // Add a feed fish function that increments the fishes current hunger level. For every point of hunger over gainWeightHungerLevel, increment weight. If over maxWeight, return a json response that contains "alive": false (also update the database. 
    public Fish feedFish(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            // Increment current hunger level
            this.currentHungerLevel -= 1;

            if (this.currentHungerLevel < gainWeightHungerLevel) {
                this.weight += 1;
            }
            if (this.currentHungerLevel > this.loseWeightHungerLevel) {
                this.weight -= 1;
            }

            // Check if the weight exceeds the maximum weight
            if (this.weight > this.maxWeight || this.weight < this.minWeight) {
                this.alive = false; // Fish dies if it exceeds max weight
            }

            // Update the fish record in the database
            String sql = "UPDATE fish SET current_hunger_level = ?, weight = ?, alive = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, this.currentHungerLevel);
                pstmt.setDouble(2, this.weight);
                pstmt.setBoolean(3, this.alive);
                pstmt.setLong(4, this.id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error updating fish in database: " + e.getMessage());
        }
        return this;
    }

    public static List<Fish> getAllDeadFish(DataSource dataSource) {
        List<Fish> deadFishList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM fish WHERE alive = FALSE";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Fish fish = new Fish();
                    fish.setId(rs.getLong("id"));
                    fish.setName(rs.getString("name"));
                    fish.setCreatedAt(rs.getTimestamp("created_at"));
                    fish.setParentFishId((Integer) rs.getObject("parent_fish_id"));
                    fish.setBase64Image(rs.getString("base64_image"));
                    fish.setImagePath(rs.getString("image_path"));
                    fish.setJson(rs.getString("json"));
                    fish.setAlive(rs.getBoolean("alive"));
                    fish.setWeight(rs.getDouble("weight"));
                    fish.setMinWeight(rs.getDouble("min_weight"));
                    fish.setMaxWeight(rs.getDouble("max_weight"));
                    fish.setCurrentHungerLevel(rs.getInt("current_hunger_level"));
                    fish.setGainWeightHungerLevel(rs.getInt("gain_weight_hunger_level"));
                    fish.setLoseWeightHungerLevel(rs.getInt("lose_weight_hunger_level"));
                    deadFishList.add(fish);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving dead fish from database: " + e.getMessage());
        }
        return deadFishList;
    }

    public static Fish getLatestFish(DataSource dataSource) {
        Fish fish = null;
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM fish WHERE alive = TRUE ORDER BY created_at DESC LIMIT 1";
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
                    fish.setAlive(rs.getBoolean("alive"));
                    fish.setWeight(rs.getDouble("weight"));
                    fish.setMinWeight(rs.getDouble("min_weight"));
                    fish.setMaxWeight(rs.getDouble("max_weight"));
                    fish.setCurrentHungerLevel(rs.getInt("current_hunger_level"));
                    fish.setGainWeightHungerLevel(rs.getInt("gain_weight_hunger_level"));
                    fish.setLoseWeightHungerLevel(rs.getInt("lose_weight_hunger_level"));
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
        String[] names = { "Goldie", "Nemo", "Dory", "Bubbles", "Splash", "Finny" };
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

        // Set default values for new fields
        fish.setAlive(true);
        fish.setWeight(1.0);
        fish.setMinWeight(0.5);
        fish.setMaxWeight(2.0);
        fish.setCurrentHungerLevel(5);
        fish.setGainWeightHungerLevel(8);
        fish.setLoseWeightHungerLevel(3);

        return fish;
    }

    public static List<Fish> getAllFish(DataSource dataSource) {
        List<Fish> allFish = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fish");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fish fish = new Fish();
                fish.setId(rs.getLong("id"));
                fish.setName(rs.getString("name"));
                fish.setCreatedAt(rs.getTimestamp("created_at"));
                fish.setParentFishId(rs.getObject("parent_fish_id") != null ? rs.getInt("parent_fish_id") : null);
                fish.setBase64Image(rs.getString("base64_image"));
                fish.setImagePath(rs.getString("image_path"));
                fish.setJson(rs.getString("json"));
                fish.setAlive(rs.getBoolean("alive"));
                fish.setWeight(rs.getDouble("weight"));
                fish.setMinWeight(rs.getDouble("min_weight"));
                fish.setMaxWeight(rs.getDouble("max_weight"));
                fish.setCurrentHungerLevel(rs.getInt("current_hunger_level"));
                fish.setGainWeightHungerLevel(rs.getInt("gain_weight_hunger_level"));
                fish.setLoseWeightHungerLevel(rs.getInt("lose_weight_hunger_level"));
                allFish.add(fish);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all fish from database: " + e.getMessage());
        }
        return allFish;
    }
    

}