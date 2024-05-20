package com.goia.feedafish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

public class Fish {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private int parentFishId;
    private String base64Image;
    private String imagePath;
    private String mood;
    private int age;
    private boolean alive;
    private double weight;
    private double minWeight;
    private double maxWeight;
    private int currentHungerLevel;
    private int gainWeightHungerLevel;
    private int loseWeightHungerLevel;
    private static final ReadWriteLock latestFishLock = new ReentrantReadWriteLock();

    public Fish() {
    }

    public Fish(Long id, String name, Timestamp createdAt, Integer parentFishId, String base64Image, String imagePath,
                String mood, Integer age, Boolean alive, Double weight, Double minWeight, Double maxWeight, Integer currentHungerLevel,
                Integer gainWeightHungerLevel, Integer loseWeightHungerLevel) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.parentFishId = parentFishId;
        this.base64Image = base64Image;
        this.imagePath = imagePath;
        this.mood = mood;
        this.age = age;
        this.alive = alive;
        this.weight = weight;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.currentHungerLevel = currentHungerLevel;
        this.gainWeightHungerLevel = gainWeightHungerLevel;
        this.loseWeightHungerLevel = loseWeightHungerLevel;
    }

    public void updateFishInfo(ResultSet rs) throws SQLException {
        this.setId(rs.getLong("id"));
        this.setName(rs.getString("name"));
        this.setCreatedAt(rs.getTimestamp("created_at"));
        this.setParentFishId(rs.getInt("parent_fish_id"));
        this.setBase64Image(rs.getString("base64_image"));
        this.setImagePath(rs.getString("image_path"));
        this.setMood(rs.getString("mood"));
        this.setAge(rs.getInt("age"));
        this.setAlive(rs.getBoolean("alive"));
        this.setWeight(rs.getDouble("weight"));
        this.setMinWeight(rs.getDouble("min_weight"));
        this.setMaxWeight(rs.getDouble("max_weight"));
        this.setCurrentHungerLevel(rs.getInt("current_hunger_level"));
        this.setGainWeightHungerLevel(rs.getInt("gain_weight_hunger_level"));
        this.setLoseWeightHungerLevel(rs.getInt("lose_weight_hunger_level"));
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

    public int getParentFishId() {
        return parentFishId;
    }

    public void setParentFishId(int parentFishId) {
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

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(double minWeight) {
        this.minWeight = minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getCurrentHungerLevel() {
        return currentHungerLevel;
    }

    public void setCurrentHungerLevel(int currentHungerLevel) {
        this.currentHungerLevel = currentHungerLevel;
    }

    public int getGainWeightHungerLevel() {
        return gainWeightHungerLevel;
    }

    public void setGainWeightHungerLevel(int gainWeightHungerLevel) {
        this.gainWeightHungerLevel = gainWeightHungerLevel;
    }

    public int getLoseWeightHungerLevel() {
        return loseWeightHungerLevel;
    }

    public void setLoseWeightHungerLevel(int loseWeightHungerLevel) {
        this.loseWeightHungerLevel = loseWeightHungerLevel;
    }

    public void updateInDatabase(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Ensure transaction management
            String sql = "UPDATE fish SET name = ?, created_at = ?, parent_fish_id = ?, base64_image = ?, image_path = ?, mood = ?, age = ?, alive = ?, weight = ?, min_weight = ?, max_weight = ?, current_hunger_level = ?, gain_weight_hunger_level = ?, lose_weight_hunger_level = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.name);
                pstmt.setTimestamp(2, this.createdAt);
                pstmt.setInt(3, this.parentFishId);
                pstmt.setString(4, this.base64Image);
                pstmt.setString(5, this.imagePath);
                pstmt.setString(6, this.mood);
                pstmt.setInt(7, this.age);
                pstmt.setBoolean(8, this.alive);
                pstmt.setDouble(9, this.weight);
                pstmt.setDouble(10, this.minWeight);
                pstmt.setDouble(11, this.maxWeight);
                pstmt.setInt(12, this.currentHungerLevel);
                pstmt.setInt(13, this.gainWeightHungerLevel);
                pstmt.setInt(14, this.loseWeightHungerLevel);
                pstmt.setLong(15, this.id);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("No rows updated. Check if the ID exists in the database.");
                } else {
                    System.out.println("Fish updated successfully.");
                }
                conn.commit(); // Commit the transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback in case of error
                System.out.println("Error updating fish in database: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error establishing database connection: " + e.getMessage());
        }
    }

    public void saveToDatabase(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO fish (name, created_at, parent_fish_id, base64_image, image_path, mood, age, alive, weight, min_weight, max_weight, current_hunger_level, gain_weight_hunger_level, lose_weight_hunger_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.name);
                pstmt.setTimestamp(2, this.createdAt);
                pstmt.setInt(3, this.parentFishId);
                pstmt.setString(4, this.base64Image);
                pstmt.setString(5, this.imagePath);
                pstmt.setString(6, this.mood);
                pstmt.setInt(7, this.age);
                pstmt.setBoolean(8, this.alive);
                pstmt.setDouble(9, this.weight);
                pstmt.setDouble(10, this.minWeight);
                pstmt.setDouble(11, this.maxWeight);
                pstmt.setInt(12, this.currentHungerLevel);
                pstmt.setInt(13, this.gainWeightHungerLevel);
                pstmt.setInt(14, this.loseWeightHungerLevel);
                pstmt.executeUpdate();
            }

            // Grab the id of the new fish
            sql = "select last_insert_rowid()";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    this.id = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving fish to database: " + e.getMessage());
        }
    }

    // Add a feed fish function that increments the fishes current hunger level. For
    // every point of hunger over gainWeightHungerLevel, increment weight. If over
    // maxWeight, return a json response that contains "alive": false (also update
    // the database.
    public Fish feedFish(DataSource dataSource) {
        System.out.println("Starting feedFish function for fish ID: " + this);
        // if (this.id == null) {
        // System.out.println("Error: Fish ID is null. Cannot feed fish without a valid
        // ID.");
        // return this;
        // }

        try {
            // Make sure we don't d multiple writes at once
            latestFishLock.writeLock().lock();
            // Make sure the fish hasn't updated since we last got it
            getLatestFish(dataSource, this);

            try (Connection conn = dataSource.getConnection()) {
                // Increment current hunger level
                this.currentHungerLevel -= 1;
                System.out.println("Current hunger level decremented. New hunger level: " + this.currentHungerLevel);

                if (this.currentHungerLevel < gainWeightHungerLevel) {
                    this.weight += 1;
                    System.out.println(
                            "Hunger level below gainWeightHungerLevel. Weight incremented. New weight: " + this.weight);
                }

                // Check if the weight exceeds the maximum weight
                if (this.weight > this.maxWeight || this.weight < this.minWeight) {
                    this.alive = false; // Fish dies if it exceeds max weight
                    System.out.println("Weight out of bounds. Fish is now dead. Alive status: " + this.alive);
                }

                // Update the fish record in the database using updateInDatabase method
                System.out.println("Fish record updated in the database.");
                this.updateInDatabase(dataSource);
            } catch (SQLException e) {
                System.out.println("Error updating fish in database: " + e.getMessage());
            }
        }
        finally
        {
            latestFishLock.writeLock().unlock();
        }
        System.out.println("Ending feedFish function for fish ID: " + this.id);
        return this;
    }

    public void makeHungrier(DataSource dataSource) {
        try {
            // Make sure we don't d multiple writes at once
            latestFishLock.writeLock().lock();
            // Make sure the fish hasn't updated since we last got it
            getLatestFish(dataSource, this);

            try (Connection conn = dataSource.getConnection()) {
                // Increment current hunger level
                this.currentHungerLevel += 1;
                System.out.println("Current hunger level incremented. New hunger level: " + this.currentHungerLevel);

                if (this.currentHungerLevel > this.loseWeightHungerLevel) {
                    this.weight -= 1 + Math.floor((this.currentHungerLevel - this.loseWeightHungerLevel) / 5.0);
                    System.out.println(
                            "Hunger level above loseWeightHungerLevel. Weight decremented. New weight: " + this.weight);
                }

                // Check if the weight exceeds the maximum weight
                if (this.weight > this.maxWeight || this.weight < this.minWeight) {
                    this.alive = false; // Fish dies if it exceeds max weight
                    System.out.println("Weight out of bounds. Fish is now dead. Alive status: " + this.alive);
                }

                // Update the fish record in the database using updateInDatabase method
                System.out.println("Fish record updated in the database.");
                this.updateInDatabase(dataSource);
            } catch (SQLException e) {
                System.out.println("Error updating fish in database: " + e.getMessage());
            }
        }
        finally
        {
            latestFishLock.writeLock().unlock();
        }
        System.out.println("Ending feedFish function for fish ID: " + this.id);
    }

    public static List<Fish> getAllDeadFish(DataSource dataSource) {
        List<Fish> deadFishList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM fish WHERE alive = FALSE";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Fish fish = new Fish();
                    fish.updateFishInfo(rs);
                    deadFishList.add(fish);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving dead fish from database: " + e.getMessage());
        }
        return deadFishList;
    }


    public static Fish getLatestFish(DataSource dataSource) {
        return getLatestFish(dataSource, null);
    }

    public static Fish getLatestFish(DataSource dataSource, Fish fish) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM fish WHERE alive = TRUE ORDER BY created_at DESC LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    if (fish == null)
                        fish = new Fish();
                    fish.updateFishInfo(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving latest fish from database: " + e.getMessage());
        }

        if (fish == null) {
            // We only make the fish at certain times
            //fish = generateRandomFish();
            //fish.saveToDatabase(dataSource);
            //fish = getLatestFish(dataSource);
        }

        return fish;
    }

    public static Fish generateRandomFish() {
        Fish fish = new Fish();
        Random random = new Random();
        String[] possibleImagePaths = { "/fish/fish_1.jpg.png", "/fish/fish_3.jpg.png", "/fish/fish_4.jpg.png",
                "/fish/fish_5.jpg.png", "/fish/fish_6.jpg.png", "/fish/fish_7.jpg.png", "/fish/fish_8.jpg.png",
                "/fish/fish_9.jpg.png", "/fish/fish_11.jpg.png", "/fish/fish_12.jpg.png", "/fish/fish_13.jpg.png",
                "/fish/fish_14.jpg.png", "/fish/fish_16.jpg.png", "/fish/fish_17.jpg.png", "/fish/fish_18.jpg.png",
                "/fish/fish_19.jpg.png", "/fish/fish_20.jpg.png", "/fish/fish_21.jpg.png", "/fish/fish_22.jpg.png",
                "/fish/fish_23.jpg.png", "/fish/fish_24.jpg.png", "/fish/fish_27.jpg.png", "/fish/fish_29.jpg.png",
                "/fish/fish_30.jpg.png", "/fish/fish_31.jpg.png", "/fish/fish_32.jpg.png", "/fish/fish_33.jpg.png",
                "/fish/fish_34.jpg.png", "/fish/fish_35.jpg.png", "/fish/fish_37.jpg.png", "/fish/fish_39.jpg.png",
                "/fish/fish_41.jpg.png", "/fish/fish_42.jpg.png", "/fish/fish_43.jpg.png", "/fish/fish_44.jpg.png",
                "/fish/fish_45.jpg.png", "/fish/fish_46.jpg.png", "/fish/fish_47.jpg.png", "/fish/fish_48.jpg.png",
                "/fish/fish_49.jpg.png", "/fish/fish_50.jpg.png", "/fish/fish_53.jpg.png", "/fish/fish_54.jpg.png",
                "/fish/fish_55.jpg.png", "/fish/fish_56.jpg.png", "/fish/fish_57.jpg.png", "/fish/fish_58.jpg.png",
                "/fish/fish_59.jpg.png", "/fish/fish_60.jpg.png", "/fish/fish_62.jpg.png", "/fish/fish_63.jpg.png",
                "/fish/fish_64.jpg.png", "/fish/fish_65.jpg.png", "/fish/fish_66.jpg.png", "/fish/fish_67.jpg.png",
                "/fish/fish_68.jpg.png", "/fish/fish_69.jpg.png", "/fish/fish_70.jpg.png", "/fish/fish_71.jpg.png",
                "/fish/fish_73.jpg.png", "/fish/fish_74.jpg.png", "/fish/fish_75.jpg.png", "/fish/fish_76.jpg.png",
                "/fish/fish_77.jpg.png", "/fish/fish_78.jpg.png", "/fish/fish_79.jpg.png", "/fish/fish_80.jpg.png",
                "/fish/fish_81.jpg.png", "/fish/fish_82.jpg.png", "/fish/fish_83.jpg.png", "/fish/fish_84.jpg.png",
                "/fish/fish_85.jpg.png", "/fish/fish_86.jpg.png", "/fish/fish_87.jpg.png", "/fish/fish_88.jpg.png",
                "/fish/fish_89.jpg.png", "/fish/fish_90.jpg.png", "/fish/fish_91.jpg.png", "/fish/fish_92.jpg.png",
                "/fish/fish_93.jpg.png", "/fish/fish_94.jpg.png", "/fish/fish_95.jpg.png", "/fish/fish_96.jpg.png",
                "/fish/fish_97.jpg.png", "/fish/fish_98.jpg.png", "/fish/fish_99.jpg.png", "/fish/fish_100.jpg.png",
                "/fish/fish_101.jpg.png", "/fish/fish_103.jpg.png", "/fish/fish_104.jpg.png", "/fish/fish_105.jpg.png",
                "/fish/fish_107.jpg.png", "/fish/fish_108.jpg.png", "/fish/fish_109.jpg.png", "/fish/fish_110.jpg.png",
                "/fish/fish_111.jpg.png", "/fish/fish_112.jpg.png", "/fish/fish_113.jpg.png", "/fish/fish_114.jpg.png",
                "/fish/fish_116.jpg.png", "/fish/fish_117.jpg.png", "/fish/fish_118.jpg.png", "/fish/fish_119.jpg.png",
                "/fish/fish_120.jpg.png", "/fish/fish_121.jpg.png", "/fish/fish_122.jpg.png", "/fish/fish_123.jpg.png",
                "/fish/fish_124.jpg.png", "/fish/fish_125.jpg.png", "/fish/fish_126.jpg.png", "/fish/fish_128.jpg.png",
                "/fish/fish_129.jpg.png", "/fish/fish_130.jpg.png", "/fish/fish_131.jpg.png", "/fish/fish_132.jpg.png",
                "/fish/fish_133.jpg.png", "/fish/fish_134.jpg.png", "/fish/fish_135.jpg.png", "/fish/fish_136.jpg.png",
                "/fish/fish_139.jpg.png", "/fish/fish_140.jpg.png", "/fish/fish_141.jpg.png", "/fish/fish_142.jpg.png",
                "/fish/fish_143.jpg.png", "/fish/fish_144.jpg.png", "/fish/fish_145.jpg.png", "/fish/fish_146.jpg.png",
                "/fish/fish_147.jpg.png", "/fish/fish_148.jpg.png", "/fish/fish_149.jpg.png", "/fish/fish_150.jpg.png",
                "/fish/fish_151.jpg.png", "/fish/fish_152.jpg.png", "/fish/fish_153.jpg.png", "/fish/fish_154.jpg.png",
                "/fish/fish_155.jpg.png", "/fish/fish_156.jpg.png", "/fish/fish_157.jpg.png", "/fish/fish_158.jpg.png",
                "/fish/fish_159.jpg.png", "/fish/fish_160.jpg.png", "/fish/fish_161.jpg.png", "/fish/fish_163.jpg.png",
                "/fish/fish_164.jpg.png", "/fish/fish_165.jpg.png", "/fish/fish_166.jpg.png", "/fish/fish_167.jpg.png",
                "/fish/fish_168.jpg.png", "/fish/fish_169.jpg.png", "/fish/fish_170.jpg.png", "/fish/fish_171.jpg.png",
                "/fish/fish_172.jpg.png", "/fish/fish_173.jpg.png", "/fish/fish_174.jpg.png", "/fish/fish_175.jpg.png",
                "/fish/fish_176.jpg.png", "/fish/fish_177.jpg.png", "/fish/fish_178.jpg.png", "/fish/fish_179.jpg.png",
                "/fish/fish_180.jpg.png", "/fish/fish_181.jpg.png", "/fish/fish_182.jpg.png", "/fish/fish_183.jpg.png",
                "/fish/fish_184.jpg.png", "/fish/fish_185.jpg.png", "/fish/fish_186.jpg.png", "/fish/fish_187.jpg.png",
                "/fish/fish_188.jpg.png", "/fish/fish_189.jpg.png", "/fish/fish_190.jpg.png", "/fish/fish_191.jpg.png",
                "/fish/fish_192.jpg.png", "/fish/fish_193.jpg.png", "/fish/fish_194.jpg.png", "/fish/fish_195.jpg.png",
                "/fish/fish_196.jpg.png", "/fish/fish_197.jpg.png", "/fish/fish_198.jpg.png", "/fish/fish_199.jpg.png",
                "/fish/fish_200.jpg.png", "/fish/fish_201.jpg.png", "/fish/fish_202.jpg.png", "/fish/fish_203.jpg.png",
                "/fish/fish_204.jpg.png", "/fish/fish_205.jpg.png", "/fish/fish_206.jpg.png", "/fish/fish_207.jpg.png",
                "/fish/fish_208.jpg.png", "/fish/fish_209.jpg.png", "/fish/fish_210.jpg.png", "/fish/fish_211.jpg.png",
                "/fish/fish_212.jpg.png", "/fish/fish_213.jpg.png", "/fish/fish_214.jpg.png", "/fish/fish_215.jpg.png",
                "/fish/fish_216.jpg.png", "/fish/fish_217.jpg.png", "/fish/fish_218.jpg.png", "/fish/fish_219.jpg.png",
                "/fish/fish_220.jpg.png", "/fish/fish_221.jpg.png", "/fish/fish_222.jpg.png", "/fish/fish_223.jpg.png",
                "/fish/fish_224.jpg.png", "/fish/fish_225.jpg.png", "/fish/fish_226.jpg.png", "/fish/fish_227.jpg.png",
                "/fish/fish_228.jpg.png", "/fish/fish_229.jpg.png", "/fish/fish_230.jpg.png", "/fish/fish_231.jpg.png",
                "/fish/fish_232.jpg.png", "/fish/fish_233.jpg.png", "/fish/fish_234.jpg.png", "/fish/fish_235.jpg.png",
                "/fish/fish_236.jpg.png", "/fish/fish_237.jpg.png", "/fish/fish_238.jpg.png", "/fish/fish_239.jpg.png",
                "/fish/fish_240.jpg.png", "/fish/fish_241.jpg.png", "/fish/fish_242.jpg.png", "/fish/fish_243.jpg.png",
                "/fish/fish_244.jpg.png", "/fish/fish_245.jpg.png", "/fish/fish_246.jpg.png", "/fish/fish_247.jpg.png",
                "/fish/fish_248.jpg.png", "/fish/fish_249.jpg.png", "/fish/fish_250.jpg.png", "/fish/fish_251.jpg.png",
                "/fish/fish_252.jpg.png", "/fish/fish_253.jpg.png", "/fish/fish_254.jpg.png", "/fish/fish_255.jpg.png",
                "/fish/fish_256.jpg.png", "/fish/fish_257.jpg.png", "/fish/fish_258.jpg.png", "/fish/fish_259.jpg.png",
                "/fish/fish_260.jpg.png", "/fish/fish_262.jpg.png", "/fish/fish_263.jpg.png", "/fish/fish_264.jpg.png",
                "/fish/fish_265.jpg.png", "/fish/fish_266.jpg.png", "/fish/fish_267.jpg.png", "/fish/fish_268.jpg.png",
                "/fish/fish_269.jpg.png", "/fish/fish_270.jpg.png", "/fish/fish_271.jpg.png", "/fish/fish_272.jpg.png",
                "/fish/fish_273.jpg.png", "/fish/fish_275.jpg.png", "/fish/fish_276.jpg.png", "/fish/fish_277.jpg.png",
                "/fish/fish_278.jpg.png", "/fish/fish_279.jpg.png", "/fish/fish_280.jpg.png", "/fish/fish_281.jpg.png",
                "/fish/fish_282.jpg.png", "/fish/fish_283.jpg.png", "/fish/fish_284.jpg.png", "/fish/fish_285.jpg.png",
                "/fish/fish_286.jpg.png", "/fish/fish_287.jpg.png", "/fish/fish_289.jpg.png", "/fish/fish_290.jpg.png",
                "/fish/fish_291.jpg.png", "/fish/fish_292.jpg.png", "/fish/fish_293.jpg.png", "/fish/fish_294.jpg.png",
                "/fish/fish_295.jpg.png", "/fish/fish_296.jpg.png", "/fish/fish_297.jpg.png" };

        // Generate random name
        String[] names = { "Goldie", "Nemo", "Dory", "Bubbles", "Splash", "Finny", "Marlin", "Gill", "Bruce", "Anchor",
                "Chum", "Coral", "Peach", "Jacques", "Deb", "Gurgle", "Bloat", "Squirt", "Crush", "Mr. Ray",
                "Nigel", "Darla", "Hank", "Destiny", "Bailey", "Charlie", "Jenny", "Gerald", "Fluke", "Rudder",
                "Becky", "Sheldon", "Tad", "Pearl", "Blenny", "Goby", "Molly", "Oscar", "Penny", "Sunny",
                "Shadow", "Spike", "Zippy", "Flash", "Glimmer", "Flicker", "Shimmer", "Twinkle", "Sparkle",
                "Glitter", "Blinky", "Wavy", "Ripple", "Surge", "Tide", "Coral", "Reef", "Sandy", "Pebble",
                "Rocky", "Shelly", "Crabby", "Claw", "Fin", "Gill", "Scales", "Whiskers", "Fins", "Gills",
                "Scaly", "Whisker", "Flounder", "Mackerel", "Tuna", "Salmon", "Trout", "Bass", "Perch",
                "Pike", "Carp", "Catfish", "Eel", "Stingray", "Jelly", "Octo", "Squid", "Kraken", "Leviathan",
                "Poseidon", "Neptune", "Aquarius", "Atlantis", "Nautilus", "Mariner", "Sailor", "Captain",
                "Pirate", "Buccaneer", "Corsair", "Seafarer", "Navigator", "Explorer", "Adventurer", "Voyager" };
        fish.setName(names[random.nextInt(names.length)]);

        // Set current timestamp as created_at
        fish.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Randomly assign a parent fish ID or null
        fish.setParentFishId(random.nextBoolean() ? 0 : random.nextInt(100) + 1);

        // Generate random base64 image string (mock)
        // fish.setBase64Image("data:image/png;base64," +
        // Base64.getEncoder().encodeToString(new byte[20]));

        // Generate random image path
        fish.setImagePath(possibleImagePaths[random.nextInt(possibleImagePaths.length)]);

        // Generate random JSON data
        fish.setMood(random.nextBoolean() ? "happy" : "sad");
        fish.setAge(random.nextInt(10) + 1);

        // Set default values for new fields
        fish.setAlive(true);
        fish.setMinWeight(random.nextDouble(10, 30));
        fish.setMaxWeight(random.nextDouble(75, 100));
        fish.setWeight(random.nextDouble(fish.getMinWeight() + 5, fish.getMaxWeight() - 5));
        fish.setGainWeightHungerLevel(random.nextInt(0, 6));
        fish.setLoseWeightHungerLevel(random.nextInt(18, 24));
        fish.setCurrentHungerLevel(random.nextInt(fish.getGainWeightHungerLevel() + 2, fish.getLoseWeightHungerLevel() - 2));

        return fish;
    }

    public static List<Fish> getAllFish(DataSource dataSource) {
        List<Fish> allFish = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fish");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fish fish = new Fish();
                fish.updateFishInfo(rs);
                allFish.add(fish);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all fish from database: " + e.getMessage());
        }
        return allFish;
    }

    @Override
    public String toString() {
        return "Fish{" +
                "id=" + (id != null ? id : "null") +
                ", name='" + (name != null ? name : "null") + '\'' +
                ", createdAt=" + (createdAt != null ? createdAt : "null") +
                ", parentFishId=" + (parentFishId) +
                ", base64Image='" + (base64Image != null ? base64Image : "null") + '\'' +
                ", imagePath='" + (imagePath != null ? imagePath : "null") + '\'' +
                ", mood='" + (mood != null ? mood : "null") + '\'' +
                ", age='" + (age) + '\'' +
                ", alive=" + (alive) +
                ", weight=" + (weight) +
                ", minWeight=" + (minWeight) +
                ", maxWeight=" + (maxWeight) +
                ", currentHungerLevel=" + (currentHungerLevel) +
                ", gainWeightHungerLevel=" + (gainWeightHungerLevel) +
                ", loseWeightHungerLevel=" + (loseWeightHungerLevel) +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":" + (id != null ? id : "null") +
                ", \"name\":\"" + (name != null ? name : "null") + "\"" +
                ", \"createdAt\":\"" + (createdAt != null ? createdAt : "null") + "\"" +
                ", \"parentFishId\":" + (parentFishId != 0 ? parentFishId : "null") +
                ", \"base64Image\":\"" + (base64Image != null ? base64Image : "null") + "\"" +
                ", \"imagePath\":\"" + (imagePath != null ? imagePath : "null") + "\"" +
                ", \"mood\":\"" + (mood != null ? mood : "null") + "\"" +
                ", \"age\":\"" + (age) + "\"" +
                ", \"alive\":" + (alive) +
                ", \"weight\":" + (weight) +
                ", \"minWeight\":" + (minWeight) +
                ", \"maxWeight\":" + (maxWeight) +
                ", \"currentHungerLevel\":" + (currentHungerLevel) +
                ", \"gainWeightHungerLevel\":" + (gainWeightHungerLevel) +
                ", \"loseWeightHungerLevel\":" + (loseWeightHungerLevel) +
                '}';
    }

    public static Object listToJson(List<Fish> deadFishList) {
        JSONArray jsonArray = new JSONArray();
        for (Fish fish : deadFishList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", fish.getId());
            jsonObject.put("name", fish.getName());
            jsonObject.put("createdAt", fish.getCreatedAt().toString());
            jsonObject.put("parentFishId", fish.getParentFishId());
            jsonObject.put("base64Image", fish.getBase64Image());
            jsonObject.put("imagePath", fish.getImagePath());
            jsonObject.put("mood", fish.getMood());
            jsonObject.put("age", fish.getAge());
            jsonObject.put("alive", fish.getAlive());
            jsonObject.put("weight", fish.getWeight());
            jsonObject.put("minWeight", fish.getMinWeight());
            jsonObject.put("maxWeight", fish.getMaxWeight());
            jsonObject.put("currentHungerLevel", fish.getCurrentHungerLevel());
            jsonObject.put("gainWeightHungerLevel", fish.getGainWeightHungerLevel());
            jsonObject.put("loseWeightHungerLevel", fish.getLoseWeightHungerLevel());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

}