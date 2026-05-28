package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "signing_algorithm")
public class SigningAlgorithmEntity {

    @Id
    @Column(name = "signing_algorithm", length = 50, nullable = false)
    private String signingAlgorithm;

    protected SigningAlgorithmEntity() {
    }

    public SigningAlgorithmEntity(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }
}