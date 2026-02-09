package com.rana.k8s.book.service;

import com.rana.k8s.book.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookQueryService {

	List<Book> findAll();

	Optional<Book> findById(String id);
}
