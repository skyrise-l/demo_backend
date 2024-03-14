package com.zju.QueryArtisan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.zju.QueryArtisan.entity"})
public class QueryArtisanApplication {
	public static void main(String[] args) {
		SpringApplication.run(QueryArtisanApplication.class, args);
	}
}
