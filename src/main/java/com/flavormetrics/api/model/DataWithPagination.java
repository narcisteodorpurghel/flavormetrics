package com.flavormetrics.api.model;

public record DataWithPagination<T>(T data, Pagination pagination) {}
