package com.rana.k8s.client.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(name = "app.book-service.url")
class LocalBookServiceClientConfig {

	@Bean
	@ReactiveBookServiceClient
	BookServiceClient reactiveBookServiceClient(
			WebClient.Builder builder,
			@Value("${app.book-service.url}") String baseUrl) {
		WebClient client = builder.baseUrl(baseUrl).build();
		return new BookServiceClientImpl(client, null);
	}

	@Bean
	RestTemplate localRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	@BlockingBookServiceClient
	BookServiceClient blockingBookServiceClient(
			RestTemplate localRestTemplate,
			@Value("${app.book-service.url}") String baseUrl) {
		return new RestTemplateBookServiceClientImpl(localRestTemplate, baseUrl);
	}

	@Bean
	RestClient localRestClient(@Value("${app.book-service.url}") String baseUrl) {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.build();
	}

	@Bean
	@RestClientBookServiceClient
	BookServiceClient restClientBookServiceClient(RestClient localRestClient,
			@Value("${app.book-service.url}") String baseUrl) {
		return new RestClientBookServiceClientImpl(localRestClient, baseUrl);
	}
}
