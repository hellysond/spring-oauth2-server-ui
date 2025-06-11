package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "authorization_request")
public class AuthorizationRequestEntity {

    @Id
    @JdbcTypeCode(Types.CHAR)
    @Column(name = "id", nullable = false, length = 36,columnDefinition = "uniqueidentifier")
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private AuthorizationEntity authorizationEntity;

    @Size(max = 255)
    @NotNull
    @Column(name = "authorization_uri", nullable = false)
    private String authorizationUri;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorization_grant_type", nullable = false)
    private AuthorizationGrantTypeEntity authorizationGrantTypeEntity;

    @Size(max = 255)
    @Column(name = "response_type")
    private String responseType;

    @Size(max = 35)
    @NotNull
    @Column(name = "client_id", nullable = false, length = 35)
    private String clientId;

    @Size(max = 255)
    @NotNull
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Size(max = 255)
    @Column(name = "state")
    private String state;

    @Size(max = 255)
    @Column(name = "authorization_request_uri")
    private String authorizationRequestUri;

    @ElementCollection
    @CollectionTable(name="authorization_request_scope", joinColumns=@JoinColumn(name="authorization_request_id"))
    @Column(name="authorization_request_scope")
    private Set<String> scopes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AuthorizationEntity getAuthorization() {
        return authorizationEntity;
    }

    public void setAuthorization(AuthorizationEntity authorizationEntity) {
        this.authorizationEntity = authorizationEntity;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public AuthorizationGrantTypeEntity getAuthorizationGrantType() {
        return authorizationGrantTypeEntity;
    }

    public void setAuthorizationGrantType(AuthorizationGrantTypeEntity authorizationGrantTypeEntity) {
        this.authorizationGrantTypeEntity = authorizationGrantTypeEntity;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public void setAuthorizationRequestUri(String authorizationRequestUri) {
        this.authorizationRequestUri = authorizationRequestUri;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
}