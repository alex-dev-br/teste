package br.com.fiap.restaurantusersapi.application.ports.inbound.list;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;

public interface ForListingUserOutput {
    Pagination<ListUserOutput> findByName(String name, Page page);
}
