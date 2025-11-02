package com.teamup.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
//fe login GET href này : https://accounts.google.com/o/oauth2/auth?scope=email profile openid&redirect_uri=http://localhost:8080/auth/login&response_type=code&client_id=67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com&approval_prompt=force