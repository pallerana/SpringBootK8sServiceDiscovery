package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
class RestTemplateBookServiceClientImpl implements BookServiceClient {

	private final RestTemplate restTemplate;
	private final String basePath;

	@Override
	public Mono<List<Book>> fetchAll() {
		return Mono.fromCallable(() -> {
					log.debug("Calling book service for /books (RestTemplate)");
					var response = restTemplate.exchange(
							basePath + "/books",
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<List<Book>>() {});
					return response.getBody() != null ? response.getBody() : List.<Book>of();
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorMap(e -> new BookServiceException("Failed to fetch books", e));
	}

	@Override
	public Mono<Book> fetchById(String id) {
		return Mono.fromCallable(() -> {
					var response = restTemplate.getForEntity(basePath + "/books/" + id, Book.class);
					return response.getBody();
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(HttpClientErrorException.NotFound.class, e -> Mono.empty())
				.onErrorMap(e -> new BookServiceException("Failed to fetch book " + id, e));
	}
}
