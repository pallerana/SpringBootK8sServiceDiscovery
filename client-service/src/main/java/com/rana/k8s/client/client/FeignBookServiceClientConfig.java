package com.rana.k8s.client.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FeignBookServiceClientConfig {

	@Bean
	@FeignBookServiceClient
	BookServiceClient feignBookServiceClient(BookServiceFeignClient feignClient) {
		return new FeignBookServiceClientImpl(feignClient);
	}
}
