package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskdemoApplication {

	public static void main(String[] args) {
		ApplicationContext ctx=SpringApplication.run(TaskdemoApplication.class, args);
	}

}
