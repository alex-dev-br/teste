package br.com.fiap.restaurantusersapi.api;

import br.com.fiap.restaurantusersapi.api.form.LoginForm;
import br.com.fiap.restaurantusersapi.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    public final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid LoginForm loginForm) {
        var authentication = loginForm.toAuthenticatorToken();
        var authenticated = authenticationManager.authenticate(authentication);
        var token = tokenService.generateToken(authenticated);
        return ResponseEntity.ok(token);
    }
}
