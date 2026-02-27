package com.claimswift.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.claimswift.document.config.FileStorageProperties;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(FileStorageProperties.class)

public class DocumentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentServiceApplication.class, args);
	System.out.println("document-service is running");
	
	}

}
