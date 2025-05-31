package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "client_settings")
public class ClientSettingsEntity {


    @GeneratedValue
    @UuidGenerator
    @Id
    @JdbcTypeCode(Types.CHAR)
    @Column(name = "id", nullable = false, length = 36,columnDefinition = "uniqueidentifier")
    private UUID id;


    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private ClientEntity clientEntity;

    @Column(name = "require_proof_key")
    private boolean requireProofKey;

    @Column(name = "require_authorization_consent")
    private boolean requireAuthorizationConsent;

    @Size(max = 255)
    @Column(name = "jwk_set_url")
    private String jwkSetUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_endpoint_authentication_signing_algorithm")
    private SigningAlgorithmEntity tokenEndpointAuthenticationSigningAlgorithmEntity;

    @Size(max = 255)
    @Column(name = "x509_certificate_subject_dn")
    private String x509CertificateSubjectDn;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isRequireProofKey() {
        return requireProofKey;
    }

    public void setRequireProofKey(boolean requireProofKey) {
        this.requireProofKey = requireProofKey;
    }

    public boolean isRequireAuthorizationConsent() {
        return requireAuthorizationConsent;
    }

    public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
        this.requireAuthorizationConsent = requireAuthorizationConsent;
    }

    public String getJwkSetUrl() {
        return jwkSetUrl;
    }

    public void setJwkSetUrl(String jwkSetUrl) {
        this.jwkSetUrl = jwkSetUrl;
    }

    public SigningAlgorithmEntity getTokenEndpointAuthenticationSigningAlgorithmEntity() {
        return tokenEndpointAuthenticationSigningAlgorithmEntity;
    }

    public void setTokenEndpointAuthenticationSigningAlgorithmEntity(SigningAlgorithmEntity tokenEndpointAuthenticationSigningAlgorithmEntity) {
        this.tokenEndpointAuthenticationSigningAlgorithmEntity = tokenEndpointAuthenticationSigningAlgorithmEntity;
    }

    public String getX509CertificateSubjectDn() {
        return x509CertificateSubjectDn;
    }

    public void setX509CertificateSubjectDn(String x509CertificateSubjectDn) {
        this.x509CertificateSubjectDn = x509CertificateSubjectDn;
    }
}