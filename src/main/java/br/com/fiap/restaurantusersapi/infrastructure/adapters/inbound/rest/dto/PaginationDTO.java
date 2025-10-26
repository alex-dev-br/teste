package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ListUserOutput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representação de paginação", name = "PaginationResponse")
public record PaginationDTO<T> (
        @Schema(example = "1", defaultValue = "1") int currentPage,
        @Schema(example = "10", defaultValue = "10") int pageSize,
        @Schema(example = "20") long totalElements,
        @Schema(example = "2") int totalPages,
        @Schema(example = "[]", description = "Lista de itens da página") List<T> content) {
    public PaginationDTO(Pagination<T> paginationResult) {
        this(paginationResult.currentPage()+1, paginationResult.pageSize(), paginationResult.totalElements(), paginationResult.totalPages(), paginationResult.content());
    }
}
