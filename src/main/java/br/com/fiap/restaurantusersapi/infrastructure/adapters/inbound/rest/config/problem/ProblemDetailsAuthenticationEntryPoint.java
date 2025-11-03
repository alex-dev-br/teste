package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

import static br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAuthenticationEntryPoint.ATTR_AUTH_ERROR;
import static br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAuthenticationEntryPoint.ERR_TOKEN_REVOKED;

public class ProblemDetailsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String ERR_TOKEN_REVOKED = "TOKEN_REVOKED";
    public static final String ATTR_AUTH_ERROR   = "auth.error";

    private final ObjectMapper om = new ObjectMapper();
    private final ProblemProperties props;

    public ProblemDetailsAuthenticationEntryPoint(ProblemProperties props) {
        this.props = props;
    }

    private URI type(String slug) {
        String base = props.baseUrl().toString();
        String sep  = base.endsWith("/") ? "" : "/";
        return URI.create(base + sep + slug);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String authError = (String) request.getAttribute(ATTR_AUTH_ERROR);

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Unauthorized");
        pd.setType(type("unauthorized"));
        pd.setDetail(authException != null ? authException.getMessage() : "Authentication required.");
        pd.setInstance(URI.create(request.getRequestURI() == null ? "/" : request.getRequestURI()));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());

        if (ERR_TOKEN_REVOKED.equals(authError)) {
            pd.setTitle("Token revoked");
            pd.setType(type("token-revoked"));
            pd.setDetail("Seu token foi revogado. Autentique-se novamente.");
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        om.writeValue(response.getWriter(), pd);
    }
}
