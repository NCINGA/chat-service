package com.ncinga.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatserviceApplication.class, args);
	}

}
