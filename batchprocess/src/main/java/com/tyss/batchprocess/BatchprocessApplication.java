package com.tyss.batchprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BatchprocessApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchprocessApplication.class, args);
	}

}
