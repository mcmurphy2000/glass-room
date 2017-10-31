package com.sln.glassroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication is the same as @Configuration @EnableAutoConfiguration @ComponentScan combined
//can use @SpringBootApplication(scanBasePackages={"com.sln.glassroom"})
//(@EnableAutoConfiguration attempts to automatically configure your Spring application based on the jar dependencies that you have added)
@SpringBootApplication
public class GlassRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlassRoomApplication.class, args);
	}
}
