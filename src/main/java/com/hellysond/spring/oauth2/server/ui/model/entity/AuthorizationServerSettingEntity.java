package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "authorization_server_settings")
public class AuthorizationServerSettingEntity {

    @Id
    @ColumnDefault("1")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Size(max = 255)
    @NotNull
    @Column(name = "authorization_endpoint", nullable = false)
    private String authorizationEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "device_authorization_endpoint", nullable = false)
    private String deviceAuthorizationEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "device_verification_endpoint", nullable = false)
    private String deviceVerificationEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_endpoint", nullable = false)
    private String tokenEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_introspection_endpoint", nullable = false)
    private String tokenIntrospectionEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_revocation_endpoint", nullable = false)
    private String tokenRevocationEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "jwk_set_endpoint", nullable = false)
    private String jwkSetEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "oidc_logout_endpoint", nullable = false)
    private String oidcLogoutEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "oidc_user_info_endpoint", nullable = false)
    private String oidcUserInfoEndpoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "oidc_client_registration_endpoint", nullable = false)
    private String oidcClientRegistrationEndpoint;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getDeviceAuthorizationEndpoint() {
        return deviceAuthorizationEndpoint;
    }

    public void setDeviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
    }

    public String getDeviceVerificationEndpoint() {
        return deviceVerificationEndpoint;
    }

    public void setDeviceVerificationEndpoint(String deviceVerificationEndpoint) {
        this.deviceVerificationEndpoint = deviceVerificationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getTokenIntrospectionEndpoint() {
        return tokenIntrospectionEndpoint;
    }

    public void setTokenIntrospectionEndpoint(String tokenIntrospectionEndpoint) {
        this.tokenIntrospectionEndpoint = tokenIntrospectionEndpoint;
    }

    public String getTokenRevocationEndpoint() {
        return tokenRevocationEndpoint;
    }

    public void setTokenRevocationEndpoint(String tokenRevocationEndpoint) {
        this.tokenRevocationEndpoint = tokenRevocationEndpoint;
    }

    public String getJwkSetEndpoint() {
        return jwkSetEndpoint;
    }

    public void setJwkSetEndpoint(String jwkSetEndpoint) {
        this.jwkSetEndpoint = jwkSetEndpoint;
    }

    public String getOidcLogoutEndpoint() {
        return oidcLogoutEndpoint;
    }

    public void setOidcLogoutEndpoint(String oidcLogoutEndpoint) {
        this.oidcLogoutEndpoint = oidcLogoutEndpoint;
    }

    public String getOidcUserInfoEndpoint() {
        return oidcUserInfoEndpoint;
    }

    public void setOidcUserInfoEndpoint(String oidcUserInfoEndpoint) {
        this.oidcUserInfoEndpoint = oidcUserInfoEndpoint;
    }

    public String getOidcClientRegistrationEndpoint() {
        return oidcClientRegistrationEndpoint;
    }

    public void setOidcClientRegistrationEndpoint(String oidcClientRegistrationEndpoint) {
        this.oidcClientRegistrationEndpoint = oidcClientRegistrationEndpoint;
    }
}