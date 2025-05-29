package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "\"authorization\"")
public class AuthorizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 35)
    @Column(name = "id", nullable = false, length = 35)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registered_client_id", nullable = false)
    private ClientEntity registeredClientEntity;

    @Size(max = 255)
    @NotNull
    @Column(name = "principal_name", nullable = false)
    private String principalName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorization_grant_type", nullable = false)
    private AuthorizationGrantTypeEntity authorizationGrantTypeEntity;

    @Size(max = 500)
    @Column(name = "state", length = 500)
    private String state;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "access_token_id", referencedColumnName = "id")
    private AccessTokenEntity accessTokenEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "id")
    private RefreshTokenEntity refreshTokenEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_code_id", referencedColumnName = "id")
    private UserCodeEntity userCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oidc_id_token_id", referencedColumnName = "id")
    private OidcIdTokenEntity oidcIdTokenEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "authorization_code_id", referencedColumnName = "id")
    private AuthorizationCodeEntity authorizationCodeEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_code_id", referencedColumnName = "id")
    private DeviceCodeEntity deviceCodeEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ElementCollection
    @CollectionTable(name="authorization_scope", joinColumns=@JoinColumn(name="authorization_id"))
    @Column(name="scope")
    private Set<String> scopes;

    @OneToOne
    private AuthorizationRequestEntity authorizationRequestEntity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ClientEntity getRegisteredClientEntity() {
        return registeredClientEntity;
    }

    public void setRegisteredClientEntity(ClientEntity registeredClientEntity) {
        this.registeredClientEntity = registeredClientEntity;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public AuthorizationGrantTypeEntity getAuthorizationGrantTypeEntity() {
        return authorizationGrantTypeEntity;
    }

    public void setAuthorizationGrantTypeEntity(AuthorizationGrantTypeEntity authorizationGrantTypeEntity) {
        this.authorizationGrantTypeEntity = authorizationGrantTypeEntity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AccessTokenEntity getAccessTokenEntity() {
        return accessTokenEntity;
    }

    public void setAccessTokenEntity(AccessTokenEntity accessTokenEntity) {
        this.accessTokenEntity = accessTokenEntity;
    }

    public RefreshTokenEntity getRefreshTokenEntity() {
        return refreshTokenEntity;
    }

    public void setRefreshTokenEntity(RefreshTokenEntity refreshTokenEntity) {
        this.refreshTokenEntity = refreshTokenEntity;
    }

    public UserCodeEntity getUserCode() {
        return userCode;
    }

    public void setUserCode(UserCodeEntity userCode) {
        this.userCode = userCode;
    }

    public DeviceCodeEntity getDeviceCodeEntity() {
        return deviceCodeEntity;
    }

    public void setDeviceCodeEntity(DeviceCodeEntity deviceCodeEntity) {
        this.deviceCodeEntity = deviceCodeEntity;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public AuthorizationRequestEntity getAuthorizationRequestEntity() {
        return authorizationRequestEntity;
    }

    public void setAuthorizationRequestEntity(AuthorizationRequestEntity authorizationRequestEntity) {
        this.authorizationRequestEntity = authorizationRequestEntity;
    }

    public AuthorizationCodeEntity getAuthorizationCodeEntity() {
        return authorizationCodeEntity;
    }

    public void setAuthorizationCodeEntity(AuthorizationCodeEntity authorizationCodeEntity) {
        this.authorizationCodeEntity = authorizationCodeEntity;
    }

    public OidcIdTokenEntity getOidcIdTokenEntity() {
        return oidcIdTokenEntity;
    }

    public void setOidcIdTokenEntity(OidcIdTokenEntity oidcIdTokenEntity) {
        this.oidcIdTokenEntity = oidcIdTokenEntity;
    }
}