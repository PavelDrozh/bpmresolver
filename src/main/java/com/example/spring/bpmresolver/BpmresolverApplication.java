package com.example.spring.bpmresolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients(basePackages = "com.example.spring.bpmresolver.clients")
public class BpmresolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpmresolverApplication.class, args);
	}

}
