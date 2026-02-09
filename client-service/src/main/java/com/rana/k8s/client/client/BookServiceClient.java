package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookServiceClient {

	Mono<List<Book>> fetchAll();

	Mono<Book> fetchById(String id);
}
