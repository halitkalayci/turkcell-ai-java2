package com.turkcell.productservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        String username = jwt.getClaimAsString("username");
        return new JwtAuthenticationToken(jwt, authorities, username);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        String roles = jwt.getClaimAsString("roles");
        if (roles == null || roles.isEmpty()) {
            return Arrays.asList();
        }

        return Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .collect(Collectors.toList());
    }
}
