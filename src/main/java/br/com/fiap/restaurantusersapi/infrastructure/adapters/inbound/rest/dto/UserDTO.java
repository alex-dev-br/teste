package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.GetRoleOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.GetUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ListRoleOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ListUserOutput;
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
        Set<String> roles,

        @Schema(example = "2025-09-28T12:00:00Z", description = "Data/hora de criação (UTC)")
        Instant createdAt,

        @Schema(example = "2025-09-28T12:34:56Z")
        Instant updatedAt,

        @Schema(implementation = AddressDTO.class)
        AddressDTO address
){
    public UserDTO(CreateUserOutput createUserOutput) {
        this(createUserOutput.uuid(), createUserOutput.name(), createUserOutput.email(),
                createUserOutput.login(), createUserOutput.roles().stream().map(CreateRoleOutput::name).collect(Collectors.toSet()),
                createUserOutput.createdAt(), createUserOutput.updatedAt(),
                createUserOutput.address() != null ? new AddressDTO(createUserOutput.address()) : null);
    }

    public UserDTO(GetUserOutput getUserOutput) {
        this(getUserOutput.uuid(), getUserOutput.name(), getUserOutput.email(), getUserOutput.login(),
                getUserOutput.roles().stream().map(GetRoleOutput::name).collect(Collectors.toSet()),
                getUserOutput.createdAt(), getUserOutput.updatedAt(),
                getUserOutput.address() != null ? new AddressDTO(getUserOutput.address()) : null);
    }

    public UserDTO(ListUserOutput listUserOutput) {
        this(listUserOutput.uuid(), listUserOutput.name(), listUserOutput.email(), listUserOutput.login(),
                listUserOutput.roles().stream().map(ListRoleOutput::name).collect(Collectors.toSet()),
                listUserOutput.createdAt(), listUserOutput.updatedAt(),
                listUserOutput.address() != null ? new AddressDTO(listUserOutput.address()) : null);
    }
}
