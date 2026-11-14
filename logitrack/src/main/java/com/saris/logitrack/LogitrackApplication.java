package com.saris.logitrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
		scanBasePackages = {
				"com.saris.logitrack",
				"model",
				"repository",
				"service",
				"controller",
				"config",
				"auth",
				"mapper",
				"exception"
		}
)
@EntityScan(basePackages = "model")
@EnableJpaRepositories(basePackages = "repository")
public class LogitrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogitrackApplication.class, args);
	}
}

