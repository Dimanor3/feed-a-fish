package com.goia.feedafish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
public class FeedafishApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedafishApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				Statement stmt = conn.createStatement();
				stmt.execute("CREATE TABLE IF NOT EXISTS fish (" +
					"id SERIAL PRIMARY KEY, " +
					"name VARCHAR(255), " +
					"created_at TIMESTAMP, " +
					"parent_fish_id INTEGER, " +
					"base64_image TEXT, " +
					"image_path TEXT, " +
					"json TEXT, " +
					"alive BOOLEAN DEFAULT TRUE, " +
					"weight DECIMAL, " +
					"min_weight DECIMAL, " +
					"max_weight DECIMAL, " +
					"current_hunger_level INTEGER, " +
					"gain_weight_hunger_level INTEGER, " +
					"lose_weight_hunger_level INTEGER" +
					")");
			}
		};
	}

}
