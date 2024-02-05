package com.prosilion.nostrrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class NostrRelayApplication extends SpringBootServletInitializer {

	/**
	 * spring-boot WAR hook
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NostrRelayApplication.class);
	}

	/**
	 * spring-boot executable JAR hook
	 */
	public static void main(String[] args) {
		SpringApplication.run(NostrRelayApplication.class, args);
	}
}
