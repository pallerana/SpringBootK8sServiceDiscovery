package com.rana.k8s.client.client;

import com.rana.k8s.client.domain.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "book-service")
public interface BookServiceFeignClient {

	@GetMapping("/books")
	List<Book> getBooks();

	@GetMapping("/books/{id}")
	Book getBook(@PathVariable("id") String id);
}
