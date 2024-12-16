package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication (scanBasePackages = {"com", "org.axonframework.springboot","org.axonframework.springboot.autoconfig", "com.nbloi.cqrses"})
@EnableScheduling
public class CqrsEsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CqrsEsApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}


	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
}
