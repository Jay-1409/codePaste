package com.example.letterbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LetterboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetterboxApplication.class, args);
	}
}
