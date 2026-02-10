package com.rana.k8s.client.client;

import org.springframework.beans.factory.annotation.Value;
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

	@Bean
	WebClient loadBalancedWebClient(WebClient.Builder builder,
			LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction) {
		return builder
				.filter(loadBalancedExchangeFilterFunction)
				.build();
	}

	@Bean
	@ReactiveBookServiceClient
	BookServiceClient reactiveBookServiceClient(WebClient loadBalancedWebClient,
			@Value("${app.book-service.service-name:book-service}") String serviceName) {
		return new BookServiceClientImpl(loadBalancedWebClient, serviceName);
	}

	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	@BlockingBookServiceClient
	BookServiceClient blockingBookServiceClient(RestTemplate loadBalancedRestTemplate,
			@Value("${app.book-service.service-name:book-service}") String serviceName) {
		String basePath = "http://" + serviceName;
		return new RestTemplateBookServiceClientImpl(loadBalancedRestTemplate, basePath);
	}

	@Bean
	RestClient loadBalancedRestClient(RestTemplate loadBalancedRestTemplate) {
		return RestClient.builder()
				.requestFactory(loadBalancedRestTemplate.getRequestFactory())
				.build();
	}

	@Bean
	@RestClientBookServiceClient
	BookServiceClient restClientBookServiceClient(RestClient loadBalancedRestClient,
			@Value("${app.book-service.service-name:book-service}") String serviceName) {
		String basePath = "http://" + serviceName;
		return new RestClientBookServiceClientImpl(loadBalancedRestClient, basePath);
	}
}
