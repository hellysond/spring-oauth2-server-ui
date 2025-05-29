package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "client_token_settings")
public class ClientTokenSettingsEntity {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @JdbcTypeCode(Types.CHAR)
    @Column(name = "id", nullable = false, length = 35,columnDefinition = "uniqueidentifier")
    private UUID id;


    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private ClientEntity clientEntity;

    @Column(name = "authorization_code_time_to_live")
    private Duration authorizationCodeTimeToLive;

    @Column(name = "access_token_time_to_live")
    private Duration accessTokenTimeToLive;

    @Size(max = 255)
    @Column(name = "access_token_format")
    private String accessTokenFormat;

    @Column(name = "device_code_time_to_live")
    private Duration deviceCodeTimeToLive;

    @Column(name = "reuse_refresh_tokens")
    private boolean reuseRefreshTokens;

    @Column(name = "refresh_token_time_to_live")
    private Duration refreshTokenTimeToLive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_token_signature_algorithm")
    private SigningAlgorithmEntity idTokenSignatureAlgorithm;

    @Column(name = "x509_certificate_bound_access_tokens")
    private boolean x509CertificateBoundAccessTokens;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Duration getAuthorizationCodeTimeToLive() {
        return authorizationCodeTimeToLive;
    }

    public void setAuthorizationCodeTimeToLive(Duration authorizationCodeTimeToLive) {
        this.authorizationCodeTimeToLive = authorizationCodeTimeToLive;
    }

    public Duration getAccessTokenTimeToLive() {
        return accessTokenTimeToLive;
    }

    public void setAccessTokenTimeToLive(Duration accessTokenTimeToLive) {
        this.accessTokenTimeToLive = accessTokenTimeToLive;
    }

    public String getAccessTokenFormat() {
        return accessTokenFormat;
    }

    public void setAccessTokenFormat(String accessTokenFormat) {
        this.accessTokenFormat = accessTokenFormat;
    }

    public Duration getDeviceCodeTimeToLive() {
        return deviceCodeTimeToLive;
    }

    public void setDeviceCodeTimeToLive(Duration deviceCodeTimeToLive) {
        this.deviceCodeTimeToLive = deviceCodeTimeToLive;
    }

    public boolean isReuseRefreshTokens() {
        return reuseRefreshTokens;
    }

    public void setReuseRefreshTokens(boolean reuseRefreshTokens) {
        this.reuseRefreshTokens = reuseRefreshTokens;
    }

    public Duration getRefreshTokenTimeToLive() {
        return refreshTokenTimeToLive;
    }

    public void setRefreshTokenTimeToLive(Duration refreshTokenTimeToLive) {
        this.refreshTokenTimeToLive = refreshTokenTimeToLive;
    }

    public SigningAlgorithmEntity getIdTokenSignatureAlgorithm() {
        return idTokenSignatureAlgorithm;
    }

    public void setIdTokenSignatureAlgorithm(SigningAlgorithmEntity idTokenSignatureAlgorithm) {
        this.idTokenSignatureAlgorithm = idTokenSignatureAlgorithm;
    }

    public boolean isX509CertificateBoundAccessTokens() {
        return x509CertificateBoundAccessTokens;
    }

    public void setX509CertificateBoundAccessTokens(boolean x509CertificateBoundAccessTokens) {
        this.x509CertificateBoundAccessTokens = x509CertificateBoundAccessTokens;
    }
}