package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class BookServiceClientImpl implements BookServiceClient {

	private final WebClient webClient;
	private final String serviceId;

	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
	private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

	@Override
	public Mono<List<Book>> fetchAll() {
		var spec = webClient.get();
		var uriSpec = serviceId != null ? spec.uri("http://{serviceId}/books", serviceId) : spec.uri("/books");
		return uriSpec
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
				.timeout(READ_TIMEOUT)
				.doOnSubscribe(s -> log.debug("Calling book service for /books"))
				.onErrorMap(e -> new BookServiceException("Failed to fetch books", e));
	}

	@Override
	public Mono<Book> fetchById(String id) {
		var spec = webClient.get();
		var uriSpec = serviceId != null ? spec.uri("http://{serviceId}/books/{id}", serviceId, id) : spec.uri("/books/{id}", id);
		return uriSpec
				.retrieve()
				.bodyToMono(Book.class)
				.timeout(READ_TIMEOUT)
				.onErrorMap(e -> new BookServiceException("Failed to fetch book " + id, e));
	}
}
