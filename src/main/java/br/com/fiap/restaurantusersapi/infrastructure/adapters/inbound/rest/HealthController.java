package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Ping", description = "Operações para verificar conectividade com a aplicação")
public class HealthController {

    @Schema(description = "Resposta do ping", example = "{\"status\":\"ok\"}", name = "PingResponse")
    public record Ping(String status){}


    @Operation(summary = "Ping")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Ping.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "409",
                    description = "Conflict",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping("/_ping")
    public Ping ping() {
        return new Ping("ok");
    }
}
