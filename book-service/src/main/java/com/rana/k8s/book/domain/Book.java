package com.rana.k8s.book.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;

@With
@Builder
public record Book(
		String id,
		@NotBlank String title,
		@NotBlank String author,
		@NotNull BigDecimal price
) {}
