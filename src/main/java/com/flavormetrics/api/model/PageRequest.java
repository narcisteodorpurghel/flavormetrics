package com.flavormetrics.api.model;

public record PageRequest<T>(
    T[] content,
    Pageable pageable,
    int totalPages,
    int totalElements,
    boolean last,
    int number,
    int size,
    int numberOfElements,
    boolean first,
    boolean empty) {}
