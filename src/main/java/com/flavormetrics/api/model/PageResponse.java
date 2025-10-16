package com.flavormetrics.api.model;

public record PageResponse(
    @Override int page, @Override int pageNumber, @Override int pageSize, int totalPages)
    implements Pagination {}
