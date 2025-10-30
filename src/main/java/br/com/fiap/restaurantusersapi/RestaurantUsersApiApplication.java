package br.com.fiap.restaurantusersapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = "br.com.fiap.restaurantusersapi")
@SpringBootApplication
public class RestaurantUsersApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantUsersApiApplication.class, args);
	}

}
