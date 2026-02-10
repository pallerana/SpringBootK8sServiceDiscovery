package com.rana.k8s.client.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(name = "app.book-service.discovery-enabled", havingValue = "true", matchIfMissing = true)
class BookServiceClientConfig {

	private static final String BOOK_SERVICE_ID = "book-service";
	private static final String BASE_PATH = "http://" + BOOK_SERVICE_ID;

	@Bean
	WebClient loadBalancedWebClient(WebClient.Builder builder,
			LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction) {
		return builder
				.filter(loadBalancedExchangeFilterFunction)
				.build();
	}

	@Bean
	@ReactiveBookServiceClient
	BookServiceClient reactiveBookServiceClient(WebClient loadBalancedWebClient) {
		return new BookServiceClientImpl(loadBalancedWebClient, BOOK_SERVICE_ID);
	}

	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	@BlockingBookServiceClient
	BookServiceClient blockingBookServiceClient(RestTemplate loadBalancedRestTemplate) {
		return new BlockingBookServiceClientImpl(loadBalancedRestTemplate, BASE_PATH);
	}

	@Bean
	RestClient loadBalancedRestClient(RestTemplate loadBalancedRestTemplate) {
		return RestClient.builder()
				.requestFactory(loadBalancedRestTemplate.getRequestFactory())
				.build();
	}

	@Bean
	@RestClientBookServiceClient
	BookServiceClient restClientBookServiceClient(RestClient loadBalancedRestClient) {
		return new RestClientBookServiceClientImpl(loadBalancedRestClient, BASE_PATH);
	}
}
