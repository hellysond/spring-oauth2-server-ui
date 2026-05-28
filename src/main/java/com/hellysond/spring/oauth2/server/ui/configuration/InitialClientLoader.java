package com.hellysond.spring.oauth2.server.ui.configuration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class InitialClientLoader implements ApplicationRunner {

    private final RegisteredClientRepository repository;
    private final PasswordEncoder passwordEncoder;

    public InitialClientLoader(
            RegisteredClientRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (repository.findByClientId("ui-server4") != null) {
            return;
        }

        RegisteredClient registeredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("ui-server4")
                        .clientIdIssuedAt(Instant.now())
                        .clientSecret(passwordEncoder.encode("secret"))
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(
                                AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(
                                AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://127.0.0.1:7860")
                        .scope("openid")
                        .scope("read")
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(5))
                                .refreshTokenTimeToLive(Duration.ofDays(1))
                                .reuseRefreshTokens(true)
                                .x509CertificateBoundAccessTokens(true)
                                .build())
                        .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .requireProofKey(false)
                                .build())
                        .build();

        repository.save(registeredClient);
    }
}