package com.rana.k8s.client.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(name = "app.book-service.discovery-enabled", havingValue = "true", matchIfMissing = true)
class BookServiceClientConfig {

	private static final String BOOK_SERVICE_ID = "book-service";

	@Bean
	WebClient loadBalancedWebClient(WebClient.Builder builder,
			LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction) {
		return builder
				.filter(loadBalancedExchangeFilterFunction)
				.build();
	}

	@Bean
	@ConditionalOnMissingBean(BookServiceClient.class)
	BookServiceClient bookServiceClient(WebClient loadBalancedWebClient) {
		return new BookServiceClientImpl(loadBalancedWebClient, BOOK_SERVICE_ID);
	}
}
