package com.Auton.gibg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
//import com.Auton.gibg.config.SwaggerConfig;

@SpringBootApplication

public class GibgApplication {
//	private final static Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

	public static void main(String[] args) {
		SpringApplication.run(GibgApplication.class, args);
	}

}
