package com.rana.k8s.book.api;

import com.rana.k8s.book.domain.Book;
import com.rana.k8s.book.service.BookQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

	private final BookQueryService bookQueryService;

	@GetMapping
	public ResponseEntity<List<Book>> list() {
		return ResponseEntity.ok(bookQueryService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> get(@PathVariable String id) {
		return bookQueryService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
