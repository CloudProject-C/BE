package com.cloudproject.TeamC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeamCApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamCApplication.class, args);
	}

}
