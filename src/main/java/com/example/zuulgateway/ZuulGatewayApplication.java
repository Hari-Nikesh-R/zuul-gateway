package com.example.zuulgateway;

import com.example.zuulgateway.utils.ThreadCleanupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextListener;


@SpringBootApplication
@EnableZuulProxy
public class ZuulGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulGatewayApplication.class, args);
	}
	@Bean
	public ServletContextListener threadCleanupListener() {
		return new ThreadCleanupListener();
	}

}
