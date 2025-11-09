package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.JwtTokenDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.LoginForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Operações de autenticação")
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    // =====================================================
    // POST /api/v1/auth
    // =====================================================
    @Operation(summary = "Autentica um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenDTO.class))),
        @ApiResponse(responseCode = "401", description = "Falha ao autenticar",
            content = @Content(mediaType = "application/problem+json")),
        @ApiResponse(responseCode = "500",
                description = "Internal Server Error",
                content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping
    public ResponseEntity<JwtTokenDTO> login(@RequestBody @Valid LoginForm loginForm) {
        var authentication = loginForm.toAuthenticatorToken();
        var authenticated = authenticationManager.authenticate(authentication);
        return ResponseEntity.ok(tokenService.generateToken(authenticated));
    }
}
