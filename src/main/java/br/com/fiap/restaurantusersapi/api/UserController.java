package br.com.fiap.restaurantusersapi.api;

import br.com.fiap.restaurantusersapi.api.form.UserCreateForm;
import br.com.fiap.restaurantusersapi.api.dto.UserDTO;
import br.com.fiap.restaurantusersapi.domain.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Users", description = "Operações de gestão de usuários")
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
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateForm in) {
        UserDTO out = service.create(in);
        URI location = URI.create("/api/v1/users/" + out.id());
        return ResponseEntity.created(location).body(out);
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
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
        UserDTO out = service.findById(id);
        return ResponseEntity.ok(out);
    }

    // =====================================================
    // GET /api/v1/users?name={name}
    // =====================================================
    @Operation(summary = "Busca um usuário pelo nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Parâmetro 'name' inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado")
    })
    @GetMapping
    public ResponseEntity<UserResponse> findByName(@RequestParam @NotBlank String name) {
        var out = service.findByName(name);
        return out.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
