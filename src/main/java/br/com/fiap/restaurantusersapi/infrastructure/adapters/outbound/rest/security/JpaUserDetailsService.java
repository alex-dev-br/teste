package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepositoryJPA repo;

    public JpaUserDetailsService(UserRepositoryJPA repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final String input = (username == null ? "" : username.trim());
        if (input.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não informado");
        }

        // Tenta primeiro por login; se não achar, tenta por e-mail
        return repo.findByLoginIgnoreCase(input)
                .or(() -> repo.findByEmailIgnoreCase(input))
                // evite ecoar o identificador para reduzir enumeração de usuários
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
