package com.lib.fiveg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.lib.fiveg.repository")
@EnableElasticsearchRepositories(basePackages = "com.lib.fiveg.elastic")
public class FivegApplication {

	public static void main(String[] args) {
		SpringApplication.run(FivegApplication.class, args);
	}
}
