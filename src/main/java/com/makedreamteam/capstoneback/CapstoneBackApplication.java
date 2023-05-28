package com.makedreamteam.capstoneback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.makedreamteam.capstoneback.domain")
public class CapstoneBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(CapstoneBackApplication.class, args);
	}

}
