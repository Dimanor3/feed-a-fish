package com.goia.feedafish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
@EnableScheduling
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
						"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"name VARCHAR(255), " +
					"created_at TIMESTAMP, " +
					"parent_fish_id INTEGER, " +
					"base64_image TEXT, " +
					"image_path TEXT, " +
					"mood TEXT, " +
					"age INTEGER, " +
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
