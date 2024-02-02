package com.org.nic.prabandh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
exclude = { SecurityAutoConfiguration.class })

public class PrabandhReportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrabandhReportsApplication.class, args);
	}

}
