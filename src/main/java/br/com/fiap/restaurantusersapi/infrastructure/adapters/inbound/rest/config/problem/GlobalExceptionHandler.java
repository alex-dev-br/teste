package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem;

import br.com.fiap.restaurantusersapi.application.domain.exception.BusinessValidationException;
import br.com.fiap.restaurantusersapi.application.domain.exception.CurrentPasswordMismatchException;
import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROP_INVALID_PARAMS = "invalidParams";

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final ProblemProperties problemProps;

    public GlobalExceptionHandler(ProblemProperties problemProps) {
        this.problemProps = problemProps;
    }

    // monta a URI do type a partir do base-url + slug
    private URI type(String slug) {
        String base = problemProps.baseUrl().toString();
        String sep = base.endsWith("/") ? "" : "/";
        return URI.create(base + sep + slug);
    }

    // 400 - Bean Validation no corpo (DTO com @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                type("invalid-data"),
                "Invalid request data",
                "One or more fields are invalid",
                null, request);

        // invalidParams: [{field, message}, ...]
        List<Map<String, Object>> invalidParams = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> invalidParam(fe.getField(), fe.getDefaultMessage()))
                .toList();
        pd.setProperty(PROP_INVALID_PARAMS, invalidParams);
        return pd;
    }

    // 400 - Erros de validação em parâmetros (@PathVariable / @RequestParam + @Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                type("invalid-parameter"),
                "Invalid request parameter",
                "One or more parameters are invalid",
                null, request);

        List<Map<String, Object>> invalidParams = ex.getConstraintViolations()
                .stream()
                .map(cv -> invalidParam(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();

        pd.setProperty(PROP_INVALID_PARAMS, invalidParams);
        return pd;
    }

    // 400 - JSON mal formado / tipo inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex,  HttpServletRequest request) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST,
                type("malformed-json"),
                "Malformed JSON",
                "Request body is not readable or has an invalid format.",
                ex, request);

        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {
                String validValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                String field = invalidFormatException.getPath().isEmpty()
                        ? "value"
                        : invalidFormatException.getPath().getFirst().getFieldName();
                String errorMessage = String.format(
                        "Invalid value for %s. Valid values are: [%s]", field, validValues);
                pd.setProperty("fields", List.of(
                        Map.of("name", field, "message", errorMessage)
                ));
            }
        }
        return pd;
    }

    // 400 - Argumento inválido genérico
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex,  HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST,
                type("invalid-argument"),
                "Invalid argument",
                "Request couldn't be processed due to  invalid input.",
                ex, request);
    }

    // 401 - Credenciais inválidas
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.UNAUTHORIZED,
                type("unauthorized"),
                "Falha ao autenticar",
                "Falha ao autenticar o usuário, verifique as credenciais e tente novamente.",
                null, request
        );
        // não usar invalidParams aqui; mensagem geral:
        pd.setProperty("reason", ex.getMessage());
        return pd;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.FORBIDDEN,
                type("forbidden"),
                "Operação não permitida",
                "Operação não permitida, verifique as permissões de acesso.",
                null, request
        );
        // não usar invalidParams aqui; mensagem geral:
        pd.setProperty("reason", ex.getMessage());
        return pd;
    }

    // 409 - Conflito de dados (Ex.: e-mail único no banco)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT,
                type("data-conflict"),
                "Data conflict",
                "A data constraint was violated.",
                ex, request);
    }

    // 422 - Regras de negócio (lista de strings)
    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleValidationException(BusinessValidationException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                type("business-rule"),
                "Violação de regra de negócio",
                "Uma ou mais regras de negócio foram violadas.",
                null, request
        );
        pd.setProperty(PROP_INVALID_PARAMS, ex.getValidationResult().errors());
        return pd;
    }

    // 422 - Regra de negócio (DomainException) → lista de strings
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleValidationException(DomainException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                type("business-rule"),
                "Violação de regra de negócio",
                "Uma ou mais regras de negócio foram violadas.",
                null, request
        );
        // padroniza como lista de strings
        pd.setProperty(PROP_INVALID_PARAMS, java.util.List.of(ex.getMessage()));
        return pd;
    }

    // 422 - Troca de senha
    @ExceptionHandler(CurrentPasswordMismatchException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleCurrentPasswordMismatch(CurrentPasswordMismatchException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                type("current-password-mismatch"),
                "Senha atual incorreta",
                "A senha atual informada não confere.",
                null, request
        );
    }

    // 500 - Genérico (pega tudo)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) throws Exception{
        String path = request.getRequestURI();
        // Deixar o Actuator tratar os próprios endpoints:
        if (path != null && path.startsWith("/actuator")) {
            throw new RuntimeException(ex);   // repropagar para o handler padrão do Spring
        }

        // Exceções que já possuem status definido (404, 405, etc) não devem virar 500
        if (ex instanceof ResponseStatusException rse) {
            throw rse;
        }
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }

        return problem(HttpStatus.INTERNAL_SERVER_ERROR,
                type("internal"),
                "Internal server error",
                "An unexpected error occurred. Please contact support.",
                ex, request);
    }

    // Helper para reduzir repetição
    private ProblemDetail problem(HttpStatus status,
                                  URI type,
                                  String title,
                                  String detail,
                                  Throwable causeOrNull,
                                  HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(type);                                        // identificador do tipo de erro (RFC 7807)
        pd.setTitle(title);                                      // título
        pd.setDetail(detail);                                    // explicação
        pd.setInstance(URI.create(request.getRequestURI()));     // qual recurso/rota falhou
        pd.setProperty("timestamp", OffsetDateTime.now());       // para debug
        if (isDev() && causeOrNull != null) {
            pd.setProperty("cause", rootMessage(causeOrNull));   // em dev, ajuda no diagnóstico
        }
        return pd;
    }

    private Map<String, Object> invalidParam(String name, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("message", message);
        return m;
    }

    private boolean isDev() {
        return "dev".equalsIgnoreCase(activeProfile);
    }

    private String rootMessage(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        return x.getMessage();
    }
}
