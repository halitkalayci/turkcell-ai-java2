package com.turkcell.gatewayserver.filter;

import com.turkcell.gatewayserver.security.JwtAuthenticationManager;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

    private final JwtAuthenticationManager authenticationManager;

    public JwtValidationGatewayFilterFactory(JwtAuthenticationManager authenticationManager) {
        super(Config.class);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            Authentication authentication = new UsernamePasswordAuthenticationToken(token, token);

            return authenticationManager.authenticate(authentication)
                    .flatMap(auth -> {
                        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
                        String userId = (String) details.get("sub");
                        String username = (String) details.get("username");
                        String roles = (String) details.get("roles");

                        var modifiedRequest = exchange.getRequest().mutate()
                                .header("X-Auth-User-Id", userId)
                                .header("X-Auth-Username", username)
                                .header("X-Auth-Roles", roles)
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }));
        };
    }

    public static class Config {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
