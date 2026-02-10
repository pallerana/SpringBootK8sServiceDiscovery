package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
class FeignBookServiceClientImpl implements BookServiceClient {

	private final BookServiceFeignClient feignClient;

	@Override
	public Mono<List<Book>> fetchAll() {
		return Mono.fromCallable(() -> {
					log.debug("Calling book service for /books (OpenFeign)");
					List<Book> result = feignClient.getBooks();
					return result != null ? result : List.<Book>of();
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorMap(e -> new BookServiceException("Failed to fetch books", e));
	}

	@Override
	public Mono<Book> fetchById(String id) {
		return Mono.fromCallable(() -> feignClient.getBook(id))
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(FeignException.NotFound.class, e -> Mono.empty())
				.onErrorMap(e -> new BookServiceException("Failed to fetch book " + id, e));
	}
}
