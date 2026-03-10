package com.turkcell.gatewayserver.security;

import com.turkcell.gatewayserver.config.JwtConfig;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationManager(JwtConfig jwtConfig) {
        SecretKey key = new SecretKeySpec(
                jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            Jwt jwt = jwtDecoder.decode(token);

            String userId = jwt.getSubject();
            String username = jwt.getClaimAsString("username");
            String rolesStr = jwt.getClaimAsString("roles");

            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            auth.setDetails(jwt.getClaims());

            return Mono.just(auth);
        } catch (JwtException e) {
            return Mono.empty();
        }
    }
}
