package com.turkcell.identityservice.controller;

import com.turkcell.identityservice.dto.TokenValidationResponse;
import com.turkcell.identityservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/auth")
@Tag(name = "Internal", description = "Internal service endpoints")
public class InternalAuthController {

    private final JwtService jwtService;

    public InternalAuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT", description = "Internal endpoint for Gateway to validate tokens")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            TokenValidationResponse response = new TokenValidationResponse(false, null, null, null);
            return ResponseEntity.ok(response);
        }

        try {
            String userId = jwtService.getUserIdFromToken(token);
            String username = jwtService.getUsernameFromToken(token);
            String rolesString = jwtService.getRolesFromToken(token);

            List<String> roles = Arrays.asList(rolesString.split(","));

            TokenValidationResponse response = new TokenValidationResponse(
                    true,
                    UUID.fromString(userId),
                    username,
                    roles
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TokenValidationResponse response = new TokenValidationResponse(false, null, null, null);
            return ResponseEntity.ok(response);
        }
    }
}
