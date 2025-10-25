package br.com.fiap.restaurantusersapi.api.dto;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserOut;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Schema(description = "Representação de usuário", name = "UserResponse")
public record UserDTO(

        @Schema(example = "2d4c0a1b-9c85-4d1e-9b6a-0a4e2db5b1f7")
        UUID id,

        @Schema(example = "Maria Silva")
        String name,

        @Schema(example = "maria.silva@mail.com")
        String email,

        @Schema(example = "mariasilva")
        String login,

        @Schema(example = "[\"CLIENT\", \"OWNER\"]", description = "Papéis de usuário: OWNER, CLIENT e ADMIN")
        Set<RoleEntity> roles,

        @Schema(example = "2025-09-28T12:00:00Z", description = "Data/hora de criação (UTC)")
        Instant createdAt,

        @Schema(example = "2025-09-28T12:34:56Z")
        Instant updatedAt,

        @Schema(implementation = AddressDTO.class)
        AddressDTO address
){
    public UserDTO(CreateUserOut createUserOutput) {
        this(createUserOutput.uuid(), createUserOutput.name(), createUserOutput.email(),
                createUserOutput.login(), createUserOutput.roles().stream().map(r -> RoleEntity.valueOf(r.name())).collect(Collectors.toSet()),
                createUserOutput.createdAt(), createUserOutput.updatedAt(),
                createUserOutput.address() != null ? new AddressDTO(createUserOutput.address()) : null);
    }
}
