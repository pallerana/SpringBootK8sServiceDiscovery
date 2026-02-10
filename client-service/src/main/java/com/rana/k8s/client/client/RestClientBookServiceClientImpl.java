package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
class RestClientBookServiceClientImpl implements BookServiceClient {

	private final RestClient restClient;
	private final String basePath;

	@Override
	public Mono<List<Book>> fetchAll() {
		return Mono.fromCallable(() -> {
					log.debug("Calling book service for /books (RestClient)");
					List<Book> body = restClient.get()
							.uri(basePath + "/books")
							.retrieve()
							.body(new ParameterizedTypeReference<>() {});
					return body != null ? body : List.<Book>of();
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorMap(e -> new BookServiceException("Failed to fetch books", e));
	}

	@Override
	public Mono<Book> fetchById(String id) {
		return Mono.fromCallable(() -> restClient.get()
						.uri(basePath + "/books/" + id)
						.retrieve()
						.body(Book.class))
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(org.springframework.web.client.HttpClientErrorException.NotFound.class, e -> Mono.empty())
				.onErrorMap(e -> new BookServiceException("Failed to fetch book " + id, e));
	}
}
