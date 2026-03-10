package com.turkcell.identityservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret;
    private String issuer;
    private String audience;
    private Expiration expiration = new Expiration();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public void setExpiration(Expiration expiration) {
        this.expiration = expiration;
    }

    public static class Expiration {
        private long accessToken;
        private long refreshToken;

        public long getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(long accessToken) {
            this.accessToken = accessToken;
        }

        public long getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(long refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
