package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.ForCreatingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.ForUpdatingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.password.ForChangingUserPassword;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.ForGettingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.delete.ForDeletingByUuid;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ForListingUserOutput;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.PaginationDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.UserDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.AdminUserCreateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.ChangePasswordForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.CustomerUserCreateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.UserUpdateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Users", description = "Operações de gestão de usuários")
@Validated
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final ForCreatingUser createUser;
    private final ForGettingUser getUser;
    private final ForListingUserOutput listUsers;
    private final ForDeletingByUuid deleteByUuid;
    private final ForChangingUserPassword changeUserPassword;
    private final ForUpdatingUser updateUser;

    public UserController(ForCreatingUser createUser,
                          ForGettingUser getUser,
                          ForListingUserOutput listUsers,
                          ForDeletingByUuid deleteByUuid,
                          ForChangingUserPassword changeUserPassword,
                          ForUpdatingUser updateUser) {
        this.createUser = createUser;
        this.getUser = getUser;
        this.listUsers = listUsers;
        this.deleteByUuid = deleteByUuid;
        this.changeUserPassword = changeUserPassword;
        this.updateUser = updateUser;
    }

    // =====================================================
    // POST /api/v1/users  (ADMIN/OWNER) — criação "interna"
    // =====================================================
    @Operation(summary = "Cria um novo usuário com permissões de dono de restaurante e/ou administrador")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "409",
                    description = "Conflito (e-mail ou login já existentes)",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "422",
                    description = "Regras de negócio violadas",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody AdminUserCreateForm in) {
        var out = createUser.create(in.toCreateUserInput());
        URI location = URI.create("/api/v1/users/" + out.uuid());
        return ResponseEntity.created(location).body(new UserDTO(out));
    }

    // =====================================================
    // POST /api/v1/users/customer  (público) — cria CUSTOMER
    // =====================================================
    @Operation(summary = "Cria um novo usuário com permissões de cliente (registro público)")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "409",
                    description = "Conflito (e-mail ou login já existentes)",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping(path = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createCustomer(@Valid @RequestBody CustomerUserCreateForm in) {
        var out = createUser.create(in.toCreateUserInput());
        URI location = URI.create("/api/v1/users/" + out.uuid());
        return ResponseEntity.created(location).body(new UserDTO(out));
    }

    // =====================================================
    // GET /api/v1/users/{uuid}
    // =====================================================
    @Operation(summary = "Busca um usuário pelo UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "ID inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{uuid}")
    public ResponseEntity<UserDTO> findById(@PathVariable("uuid") UUID id) {
        return getUser.findByUuid(id)
                .map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =====================================================
    // GET /api/v1/users?name={name}
    // =====================================================
    @Operation(summary = "Busca usuários pelo nome (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Paginação de usuário(s) encontrado(s)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Parâmetro 'name' inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaginationDTO<UserDTO>> findByName(
            @RequestParam @NotBlank String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageDomain = new Page(page < 1 ? 1 : page - 1, size < 1 ? 10 : size);
        var result = listUsers.findByName(name, pageDomain).mapItems(UserDTO::new);
        return ResponseEntity.ok(new PaginationDTO<>(result));
    }

    // =====================================================
    // DELETE /api/v1/users/{uuid}
    // =====================================================
    @Operation(summary = "Exclui um usuário pelo UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "400",
                    description = "UUID inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @DeleteMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER') or authentication.principal.uuid == #uuid")
    public ResponseEntity<Void> deleteUser(@PathVariable("uuid") UUID uuid) {
        deleteByUuid.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // PUT /api/v1/users/change-password  (próprio usuário)
    // =====================================================
    @Operation(summary = "Altera senha do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual inválida ou payload inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403", description = "Permissão negada",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "422", description = "Erro nas regras de negócio",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500", description = "Erro inesperado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails authUser,
                                               @Valid @RequestBody ChangePasswordForm form) {
        changeUserPassword.changeUserPassword(form.toChangePasswordInput(((UserEntity) authUser).getUuid()));
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // PUT /api/v1/users  (próprio usuário)
    // =====================================================
    @Operation(summary = "Atualiza os dados (exceto senha) do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403", description = "Usuário não pode alterar outro usuário",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "422", description = "Regras de negócio: email/login já existentes",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500", description = "Erro inesperado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal UserDetails authUser,
                                              @Valid @RequestBody UserUpdateForm form) {
        var out = updateUser.update(form.toUpdateUserInput(((UserEntity) authUser).getUuid()));
        return ResponseEntity.ok(new UserDTO(out));
    }

    // =====================================================
    // PUT /api/v1/users/{uuid}  (ADMIN/OWNER atualiza outro)
    // =====================================================
    @Operation(summary = "Atualiza outro usuário (ADMIN/OWNER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Payload inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro inesperado no servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> updateUserByAdmin(@PathVariable("uuid") UUID uuid,
                                                     @Valid @RequestBody UserUpdateForm form) {
        var out = updateUser.update(form.toUpdateUserInput(uuid));
        return ResponseEntity.ok(new UserDTO(out));
    }
}
