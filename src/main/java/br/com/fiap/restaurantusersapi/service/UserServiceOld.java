package br.com.fiap.restaurantusersapi.service;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.AddressDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.UserDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.UserCreateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.application.domain.exception.BusinessValidationException;
import br.com.fiap.restaurantusersapi.service.validator.ValidatorOld;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class UserServiceOld {

    private final UserRepositoryJPA repo;

    private final PasswordEncoder encoder;

    @Qualifier("userCreatorValidatorOld")
    private final ValidatorOld<UserEntity> userCreatorValidator;

    public UserServiceOld(UserRepositoryJPA repo,
                          PasswordEncoder encoder,
                          @Qualifier("userCreatorValidatorOld") ValidatorOld<UserEntity> userCreatorValidator) {
        this.repo = repo;
        this.encoder = encoder;
        this.userCreatorValidator = userCreatorValidator;
    }

    @Transactional
    public UserDTO create(UserCreateForm in) {
        var user = new UserEntity();
        user.setName(in.name());
        user.setEmail(in.email());
        user.setLogin(in.login());
        user.setPasswordHash(encoder.encode(in.password()));

        // Papel padrão => CLIENT
        Set<RoleEntity> rolesFromForm = (in.roles() != null && !in.roles().isEmpty())
                ? EnumSet.copyOf(in.roles())
                : EnumSet.of(RoleEntity.CLIENT);
        user.setRoles(rolesFromForm);

        // Monta address apenas se vier no form
        AddressEntity address = null;
        if (in.address() != null) {
            address = new AddressEntity();
            address.setStreet(in.address().street());
            address.setNumber(in.address().number());
            address.setComplement(in.address().complement());
            address.setCity(in.address().city());
            address.setNeighborhood(in.address().neighborhood());
            address.setState(in.address().state());
            address.setZipCode(in.address().zipCode());

            // relação bidirecional
            address.setUser(user);
            user.setAddress(address);
        }

        var result = userCreatorValidator.validate(user);
        if (result.isInvalid()) {
            throw new BusinessValidationException(result);
        }

        try {
            user = repo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw e;    // será tratado pelo GlobalExceptionHandler => retorna 409 Conflict
        }
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(UUID id) {
        var user = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com o ID: " + id));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAllByName(String name) {
        return repo.findAllByNameIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    private UserDTO toResponse(UserEntity u) {
        return new UserDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getLogin(),
                u.getRoles(),
                u.getCreatedAt(),
                u.getUpdatedAt(),
                u.getAddress() == null ? null : new AddressDTO(u.getAddress())
        );
    }

    @Transactional
    public void deleteByUuid(UUID uuid) {
        repo.deleteById(uuid);
    }
}
