package com.rana.k8s.client.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnBean(BookServiceClient.class)
class BookServiceClientFactory {

	static final String REACTIVE = "reactive";
	static final String REST_TEMPLATE = "rest-template";
	static final String REST_CLIENT = "rest-client";

	@Bean
	@Primary
	BookServiceClient bookServiceClient(
			@Value("${app.book-service.client-type:" + REACTIVE + "}") String clientType,
			@ReactiveBookServiceClient BookServiceClient reactiveClient,
			@BlockingBookServiceClient BookServiceClient blockingClient,
			@RestClientBookServiceClient BookServiceClient restClientBookServiceClient) {
		return switch (clientType.toLowerCase()) {
			case REST_TEMPLATE, "blocking" -> blockingClient;
			case REST_CLIENT -> restClientBookServiceClient;
			default -> reactiveClient;
		};
	}
}
