package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "signing_algorithm")
public class SigningAlgorithmEntity {

    @UuidGenerator
    @Id
    @JdbcTypeCode(Types.CHAR)
    @Column(name = "id", nullable = false, length = 36,columnDefinition = "uniqueidentifier")
    private UUID id;

    @Size(max = 255)
    @Column(name = "signing_algorithm")
    private String signingAlgorithm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }
}