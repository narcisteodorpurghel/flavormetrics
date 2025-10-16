package com.flavormetrics.api.model;

public interface Pagination {
  int pageSize();
  int pageNumber();
  int totalPages();
}
