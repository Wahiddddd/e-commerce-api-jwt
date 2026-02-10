package com.market.e_commerce_JWT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceJwtApplication.class, args);
	}

}
