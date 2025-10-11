package br.com.fiap.restaurantusersapi.domain;

import br.com.fiap.restaurantusersapi.api.dto.UserCreateRequest;
import br.com.fiap.restaurantusersapi.api.dto.UserResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public UserResponse create(UserCreateRequest in) {
        var user = new User();
        user.setName(in.name());
        user.setEmail(in.email());
        user.setLogin(in.login());
        user.setPasswordHash(in.password());  // futuramente aplicar hash (BCrypt)
        user.setRole("CLIENT");               // valor padrão inicial
        user.setAddressSummary(null);

        try {
            user = repo.save(user);
        } catch (DataIntegrityViolationException e) {
            // será tratado pelo GlobalExceptionHandler => retorna 409 Conflict
            throw e;
        }

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(java.util.UUID id) {
        var user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> findByName(String name) {
        var optionalUser = repo.findByNameIgnoreCase(name);
        return optionalUser.map(this::toResponse);
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getLogin(),
                u.getRole(),
                u.getUpdatedAt(),
                u.getAddressSummary()
        );
    }
}
