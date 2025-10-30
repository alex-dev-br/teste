package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.service.UserService;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.PaginationDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.PasswordChangeDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.UserDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.UserUpdateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.UserCreateForm;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Users", description = "Operações de gestão de usuários")
@Validated
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // =====================================================
    // POST /api/v1/users
    // =====================================================
    @Operation(summary = "Cria um novo usuário")
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth") /*TODO verificar depois se precisa esta autenticado para se cadastras, n faz muito sentido*/
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateForm in) {
        var createUserOutput = service.create(in.toCreateUserInput());
        URI location = URI.create("/api/v1/users/" + createUserOutput.uuid());
        return ResponseEntity.created(location).body(new UserDTO(createUserOutput));
    }

    // =====================================================
    // GET /api/v1/users/{id}
    // =====================================================
    @Operation(summary = "Busca um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "ID inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
        var output = service.findByUuid(id);
        return output.map(getUserOutput -> ResponseEntity.ok(new UserDTO(getUserOutput))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =====================================================
    // GET /api/v1/users?name={name}
    // =====================================================
    @Operation(summary = "Busca um usuário pelo nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Paginação de usuário(s) encontrado(s)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Parâmetro 'name' inválido",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaginationDTO<UserDTO>> findByName(
            @RequestParam @NotBlank String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageDomain = new Page(page < 1 ? 1 : page-1, size < 1 ? 10 : size);
        var paginationResult = service.findByName(name, pageDomain).mapItems(UserDTO::new);
        return ResponseEntity.ok(new PaginationDTO<>(paginationResult));
    }

    // =====================================================
    // DELETE /api/v1/users/{uuid}
    // =====================================================
    @Operation(summary = "Exclui um usuário pelo Uuid")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "400",
                description = "UUID inválido",
                content = @Content(mediaType = "application/problem+json")),
    })
    @DeleteMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUser(@PathVariable("uuid") UUID uuid) {
        service.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Troca a própria senha do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400",
                    description = "Senha atual inválida ou payload inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "403",
                    description = "Usuário não pode alterar senha de outro usuário",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "422",
                    description = "Regras de negócio: senha não atende requisitos",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro inesperado no servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PatchMapping("/{uuid}/password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID uuid,
            @Valid @RequestBody PasswordChangeDTO form
    ) {
        service.changePassword(uuid, form);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza os dados (exceto senha) do próprio usuário ou se admin")
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
                    description = "Usuário não pode alterar outro usuário",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "422",
                    description = "Regras de negócio: email/login já existentes",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Erro inesperado no servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserUpdateForm form
    ) {
        var output = service.update(uuid, form);
        return ResponseEntity.ok(new UserDTO(output));
    }

}
