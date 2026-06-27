package com.mutrix.prepa;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition
@EnableAsync
@EnableScheduling
public class PrepaConcourApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrepaConcourApplication.class, args);
	}

}
