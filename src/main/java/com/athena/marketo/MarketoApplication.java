package com.athena.marketo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(basePackages= "com.athena")
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class MarketoApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(MarketoApplication.class, args);
	}

}
