package com.rana.k8s.book.service;

import com.rana.k8s.book.domain.Book;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryBookQueryService implements BookQueryService {

	private static final List<Book> CATALOG = List.of(
			Book.builder().id("1").title("Kubernetes in Action").author("Marko Luksa").price(new BigDecimal("49.99")).build(),
			Book.builder().id("2").title("Spring Boot in Practice").author("Som Sharma").price(new BigDecimal("44.99")).build(),
			Book.builder().id("3").title("Cloud Native Java").author("Josh Long").price(new BigDecimal("54.99")).build()
	);

	private final Map<String, Book> byId = new ConcurrentHashMap<>();

	public InMemoryBookQueryService() {
		CATALOG.forEach(b -> byId.put(b.id(), b));
	}

	@Override
	public List<Book> findAll() {
		return List.copyOf(byId.values());
	}

	@Override
	public Optional<Book> findById(String id) {
		return Optional.ofNullable(byId.get(id));
	}
}
