package com.infy.pintrest.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final String PROJECT_DIR = System.getProperty("user.dir");

	private static String toUri(String relativePath) {
		Path path = Paths.get(PROJECT_DIR, relativePath).toAbsolutePath().normalize();
		String uri = path.toUri().toString();
		return uri.endsWith("/") ? uri : uri + "/";
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**").addResourceLocations(toUri("uploads"));
		registry.addResourceHandler("/board-covers/**").addResourceLocations(toUri("board-covers"));
		registry.addResourceHandler("/profile-uploads/**").addResourceLocations(toUri("profile-uploads"));
		registry.addResourceHandler("/pins-uploads/**").addResourceLocations(toUri("pins-uploads"));
		registry.addResourceHandler("/ads-uploads/**").addResourceLocations(toUri("ads-uploads"));
	}
}

