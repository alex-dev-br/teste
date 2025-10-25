package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {}
