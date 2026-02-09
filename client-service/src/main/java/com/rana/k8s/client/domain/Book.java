package com.rana.k8s.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Book(String id, String title, String author, BigDecimal price) {}
