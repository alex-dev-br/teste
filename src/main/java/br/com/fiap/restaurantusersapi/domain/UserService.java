package br.com.fiap.restaurantusersapi.domain;

import br.com.fiap.restaurantusersapi.api.dto.AddressDTO;
import br.com.fiap.restaurantusersapi.api.form.UserCreateForm;
import br.com.fiap.restaurantusersapi.api.dto.UserDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repo;
    private final AddressRepository addressRepository;

    public UserService(UserRepository repo, AddressRepository addressRepository) {
        this.repo = repo;
        this.addressRepository = addressRepository;
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
}
