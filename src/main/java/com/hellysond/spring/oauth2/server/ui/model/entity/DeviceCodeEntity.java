package com.hellysond.spring.oauth2.server.ui.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "device_code")
public class DeviceCodeEntity {

    @Id
    @Size(max = 35)
    @Column(name = "id", nullable = false, length = 35)
    private String id;

    @Size(max = 4000)
    @ColumnDefault("NULL")
    @Column(name = "devicecode_value", length = 4000)
    private String deviceCodeValue;

    @Column(name = "device_code_issued_at")
    private Instant deviceCodeIssuedAt;

    @Column(name = "device_code_expires_at")
    private Instant deviceCodeExpiresAt;

    @Size(max = 2000)
    @ColumnDefault("NULL")
    @Column(name = "device_code_metadata", length = 2000)
    private String deviceCodeMetadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceCodeValue() {
        return deviceCodeValue;
    }

    public void setDeviceCodeValue(String deviceCodeValue) {
        this.deviceCodeValue = deviceCodeValue;
    }

    public Instant getDeviceCodeIssuedAt() {
        return deviceCodeIssuedAt;
    }

    public void setDeviceCodeIssuedAt(Instant deviceCodeIssuedAt) {
        this.deviceCodeIssuedAt = deviceCodeIssuedAt;
    }

    public Instant getDeviceCodeExpiresAt() {
        return deviceCodeExpiresAt;
    }

    public void setDeviceCodeExpiresAt(Instant deviceCodeExpiresAt) {
        this.deviceCodeExpiresAt = deviceCodeExpiresAt;
    }

    public String getDeviceCodeMetadata() {
        return deviceCodeMetadata;
    }

    public void setDeviceCodeMetadata(String deviceCodeMetadata) {
        this.deviceCodeMetadata = deviceCodeMetadata;
    }
}