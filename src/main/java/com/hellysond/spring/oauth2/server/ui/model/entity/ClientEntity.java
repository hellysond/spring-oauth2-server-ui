package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @NotNull
    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt;

    @Size(max = 255)
    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Size(max = 255)
    @NotNull
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "client_redirect_uris",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "redirect_uri")
    private Set<String> redirectUris = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "client_scopes",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "scope")
    private Set<String> scopes = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "client_post_logout_redirect_uris",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "post_logout_redirect_uri")
    private Set<String> postLogoutRedirectUris = new HashSet<>();

    @OneToOne(mappedBy = "clientEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientSettingsEntity settings;

    @OneToOne(mappedBy = "clientEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientTokenSettingsEntity tokenSettings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_authentication_methods",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authentication_method")
    )
    private Set<AuthenticationMethodEntity> authenticationMethods = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_authorization_grant_types",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authorization_grant_type")
    )
    private Set<AuthorizationGrantTypeEntity> authorizationGrantTypes = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Instant getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Instant getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(Instant clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public Set<String> getPostLogoutRedirectUris() {
        return postLogoutRedirectUris;
    }

    public void setPostLogoutRedirectUris(Set<String> postLogoutRedirectUris) {
        this.postLogoutRedirectUris = postLogoutRedirectUris;
    }

    public ClientSettingsEntity getSettings() {
        return settings;
    }

    public void setSettings(ClientSettingsEntity settings) {
        this.settings = settings;
    }

    public ClientTokenSettingsEntity getTokenSettings() {
        return tokenSettings;
    }

    public void setTokenSettings(ClientTokenSettingsEntity tokenSettings) {
        this.tokenSettings = tokenSettings;
    }

    public Set<AuthenticationMethodEntity> getAuthenticationMethods() {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(Set<AuthenticationMethodEntity> authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    public Set<AuthorizationGrantTypeEntity> getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public void setAuthorizationGrantTypes(Set<AuthorizationGrantTypeEntity> authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
    }
}