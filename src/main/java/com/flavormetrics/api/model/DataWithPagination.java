package com.flavormetrics.api.model;

public record DataWithPagination<T>(T data, int pageSize, int pageNumber, int totalPages) {}
