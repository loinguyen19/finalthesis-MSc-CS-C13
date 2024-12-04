package com.nbloi.cqrses;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.ModelMap;

@SpringBootApplication (scanBasePackages = {"com", "org.axonframework.springboot","org.axonframework.springboot.autoconfig", "com.nbloi.cqrses"})
public class CqrsEsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CqrsEsApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
