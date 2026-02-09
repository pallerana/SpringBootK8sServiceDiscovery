package com.rana.k8s.client.api;

import com.rana.k8s.client.client.BookServiceClient;
import com.rana.k8s.client.client.BookServiceException;
import com.rana.k8s.client.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

	private final BookServiceClient bookServiceClient;

	@GetMapping("/books")
	public Mono<ResponseEntity<List<Book>>> listBooks() {
		return bookServiceClient.fetchAll()
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.ok(List.of()));
	}

	@GetMapping("/books/{id}")
	public Mono<ResponseEntity<Book>> getBook(@PathVariable String id) {
		return bookServiceClient.fetchById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}

	@ExceptionHandler(BookServiceException.class)
	public ResponseEntity<ErrorResponse> handleBookServiceException(BookServiceException ex) {
		return ResponseEntity.status(502).body(new ErrorResponse(ex.getMessage()));
	}

	public record ErrorResponse(String error) {}
}
