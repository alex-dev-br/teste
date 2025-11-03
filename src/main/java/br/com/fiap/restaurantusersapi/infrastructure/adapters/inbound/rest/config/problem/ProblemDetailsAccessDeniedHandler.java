package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

public class ProblemDetailsAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om = new ObjectMapper();
    private final ProblemProperties props;

    public ProblemDetailsAccessDeniedHandler(ProblemProperties props) {
        this.props = props;
    }

    private URI type(String slug) {
        String base = props.baseUrl().toString();
        String sep  = base.endsWith("/") ? "" : "/";
        return URI.create(base + sep + slug);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        var pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail(ex.getMessage());
        pd.setType(type("forbidden"));
        pd.setInstance(URI.create(request.getRequestURI() == null ? "/" : request.getRequestURI()));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());

        if (ex instanceof PasswordChangeRequiredException) {
            pd.setTitle("Password change required");
            pd.setType(type("password-change-required"));
            pd.setDetail("Você precisa atualizar sua senha antes de continuar.");
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        om.writeValue(response.getWriter(), pd);
    }
}
