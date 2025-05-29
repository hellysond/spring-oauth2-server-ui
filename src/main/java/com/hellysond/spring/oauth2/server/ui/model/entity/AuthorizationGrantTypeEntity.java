package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "authorization_grant_type")
public class AuthorizationGrantTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 35)
    @Column(name = "id", nullable = false, length = 35)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "authorization_grant_type", nullable = false)
    private String authorizationGrantType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }
}