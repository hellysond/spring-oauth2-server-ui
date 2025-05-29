package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "authentication_method")
public class AuthenticationMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 35)
    @Column(name = "id", nullable = false, length = 35)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "authentication_method", nullable = false)
    private String authenticationMethod;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }
}