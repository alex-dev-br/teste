package br.com.fiap.restaurantusersapi.service;

import br.com.fiap.restaurantusersapi.api.dto.AddressDTO;
import br.com.fiap.restaurantusersapi.api.dto.UserDTO;
import br.com.fiap.restaurantusersapi.api.form.UserCreateForm;
import br.com.fiap.restaurantusersapi.domain.Address;
import br.com.fiap.restaurantusersapi.domain.AddressRepository;
import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.domain.UserRepository;
import br.com.fiap.restaurantusersapi.domain.exception.BusinessValidationException;
import br.com.fiap.restaurantusersapi.service.validator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repo;
    private final AddressRepository addressRepository;

    @Qualifier("userCreatorValidator")
    private final Validator<User> userCreatorValidator;

    public UserService(UserRepository repo, AddressRepository addressRepository, Validator<User> userCreatorValidator) {
        this.repo = repo;
        this.addressRepository = addressRepository;
        this.userCreatorValidator = userCreatorValidator;
    }

    @Transactional
    public UserDTO create(UserCreateForm in) {
        var address = new Address();
        if (in.address() != null) {
            address.setStreet(in.address().street());
            address.setNumber(in.address().number());
            address.setComplement(in.address().complement());
            address.setCity(in.address().city());
            address.setNeighborhood(in.address().neighborhood());
            address.setState(in.address().state());
            address.setZipCode(in.address().zipCode());
        }

        var user = new User();
        user.setName(in.name());
        user.setEmail(in.email());
        user.setLogin(in.login());
        user.setPasswordHash(in.password());  // futuramente aplicar hash (BCrypt)
        user.setRole("CLIENT");               // valor padrão inicial

        address.setUser(user);

        var result = userCreatorValidator.validate(user);
        if (result.isInvalid()) {
            throw new BusinessValidationException(result);
        }

        try {
            user = repo.save(user);
            user.setAddress(addressRepository.save(address));
        } catch (DataIntegrityViolationException e) {
            // será tratado pelo GlobalExceptionHandler => retorna 409 Conflict
            throw e;
        }

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(java.util.UUID id) {
        var user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByName(String name) {
        var optionalUser = repo.findByNameIgnoreCase(name);
        return optionalUser.map(this::toResponse);
    }

    private UserDTO toResponse(User u) {
        return new UserDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getLogin(),
                u.getRole(),
                u.getUpdatedAt(),
                u.getAddress() == null ? null : new AddressDTO(u.getAddress())
        );
    }

    @Transactional
    public void deleteByUuid(UUID uuid) {
        repo.deleteById(uuid);
    }
}
