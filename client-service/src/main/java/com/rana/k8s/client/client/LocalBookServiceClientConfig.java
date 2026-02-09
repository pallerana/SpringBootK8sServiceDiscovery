package com.rana.k8s.client.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(name = "app.book-service.url")
class LocalBookServiceClientConfig {

	@Bean
	BookServiceClient bookServiceClient(
			WebClient.Builder builder,
			@Value("${app.book-service.url}") String baseUrl) {
		WebClient client = builder.baseUrl(baseUrl).build();
		return new BookServiceClientImpl(client, null);
	}
}
