package br.com.fiap.restaurantusersapi.application.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Pagination<T>(int currentPage, int pageSize, long totalElements, int totalPages, List<T> content) {

    public <R> Pagination<R> mapItems(Function<T, R> mapper) {
        return new Pagination<>(currentPage, pageSize, totalElements, totalPages, content.stream().map(mapper).toList());
    }
}
