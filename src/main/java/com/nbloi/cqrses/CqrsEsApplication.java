package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication (scanBasePackages = {"com", "org.axonframework.springboot","org.axonframework.springboot.autoconfig", "com.nbloi.cqrses"})
public class CqrsEsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CqrsEsApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
