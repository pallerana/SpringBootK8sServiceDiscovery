package com.rana.k8s.client.client;

public class BookServiceException extends RuntimeException {

	public BookServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
