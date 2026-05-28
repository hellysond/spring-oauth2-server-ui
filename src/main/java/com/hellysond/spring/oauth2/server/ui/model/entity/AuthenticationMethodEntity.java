package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "authentication_method")
public class AuthenticationMethodEntity {

    @Id
    @Column(name = "authentication_method", length = 50, nullable = false)
    private String authenticationMethod;

    protected AuthenticationMethodEntity() {
    }

    public AuthenticationMethodEntity(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }
}