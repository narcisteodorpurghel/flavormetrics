package com.flavormetrics.api.model;

public record Pageable(
    int pageNumber, int pageSize, Sort sort, int offset, boolean paged, boolean unpaged) {}
