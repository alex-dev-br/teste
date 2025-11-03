package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAccessDeniedHandler;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAuthenticationEntryPoint;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemProperties;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security.TokenExtractorService;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Handlers que usam ProblemDetails + ProblemProperties
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(ProblemProperties props) {
        return new ProblemDetailsAuthenticationEntryPoint(props);
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler(ProblemProperties props) {
        return new ProblemDetailsAccessDeniedHandler(props);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            TokenService tokenService,
                                            TokenExtractorService tokenExtractor,
                                            UserRepositoryJPA userRepositoryJPA,
                                            UserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            AccessDeniedHandler accessDeniedHandler) throws Exception {

        var daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);
        http.authenticationProvider(daoProvider);

        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(reg -> reg
                        // Preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Docs & health & ping
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/api/v1/_ping"
                        ).permitAll()

                        // Auth (login) — público
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth", "/api/v1/auth/login").permitAll()
                        // (opcional) outros endpoints sob /auth
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Cadastro público de CUSTOMER
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/customer").permitAll()

                        // Qualquer outra rota /users/** requer autenticação
                        .requestMatchers("/api/v1/users/**").authenticated()

                        // Restante: autenticado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new TokenAuthenticationFilter(tokenExtractor, tokenService, userRepositoryJPA),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAfter(new ForcePasswordPolicyFilter(), TokenAuthenticationFilter.class);

        return http.build();
    }
}
