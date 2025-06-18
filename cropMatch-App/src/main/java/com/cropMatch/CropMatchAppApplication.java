package com.cropMatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CropMatchAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CropMatchAppApplication.class, args);
	}
}
