package br.com.fiap.restaurantusersapi.api.problem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // URIs locais (placeholders) para identificar cada tipo de erro (RFC 7807 - "type")
    private static final URI TYPE_INVALID_DATA      = URI.create("http://localhost:8080/erros/invalid-data");
    private static final URI TYPE_INVALID_PARAMETER = URI.create("http://localhost:8080/erros/invalid-parameter");
    private static final URI TYPE_MALFORMED_JSON    = URI.create("http://localhost:8080/erros/malformed-json");
    private static final URI TYPE_DATA_CONFLICT     = URI.create("http://localhost:8080/erros/data-conflict");
    private static final URI TYPE_INVALID_ARGUMENT  = URI.create("http://localhost:8080/erros/invalid-argument");
    private static final URI TYPE_INTERNAL_ERROR    = URI.create("http://localhost:8080/erros/internal");

    private static final String PROP_INVALID_PARAMS = "invalidParams";

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    // 400 - Bean Validation no corpo (DTO com @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                TYPE_INVALID_DATA,
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
                TYPE_INVALID_PARAMETER,
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
                TYPE_MALFORMED_JSON,
                "Malformed JSON",
                "Request body is not readable or has an invalid format.",
                ex, request);
        return pd;
    }

    // 400 - Argumento inválido genérico
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex,  HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST,
                TYPE_INVALID_ARGUMENT,
                "Invalid argument",
                "Request couldn't be processed due to  invalid input.",
                ex, request);
    }

    // 409 - Conflito de dados (Ex.: e-mail único no banco)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT,
                TYPE_DATA_CONFLICT,
                "Data conflict",
                "A data constraint was violated.",
                ex, request);
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
                TYPE_INTERNAL_ERROR,
                "Internal server error",
                "An unexpected error occurred. Please contact support.",
                ex, request);
    }


    // Para diminuir repetição:
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
