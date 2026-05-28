package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "authorization_grant_type")
public class AuthorizationGrantTypeEntity {

    @Id
    @Column(name = "authorization_grant_type", length = 50, nullable = false)
    private String authorizationGrantType;

    protected AuthorizationGrantTypeEntity() {
    }

    public AuthorizationGrantTypeEntity(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }
}