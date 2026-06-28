package com.example.codePost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CodePostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodePostApplication.class, args);
	}
}
