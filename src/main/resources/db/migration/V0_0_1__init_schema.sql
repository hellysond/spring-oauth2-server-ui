CREATE TABLE "user" (
    id CHAR(36) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled INT CHECK (enabled IN (0, 1)),
    email VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE signing_algorithm (
    signing_algorithm VARCHAR(50) NOT NULL,
    PRIMARY KEY (signing_algorithm)
);

CREATE TABLE client (
    id CHAR(36) NOT NULL,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret VARCHAR(255) DEFAULT NULL,
    client_secret_expires_at TIMESTAMP DEFAULT NULL,
    client_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    PRIMARY KEY (id)
);

CREATE INDEX idx_client_name
ON client(client_name);

CREATE TABLE authentication_method (
    authentication_method VARCHAR(50) NOT NULL,
    PRIMARY KEY (authentication_method)
);

CREATE TABLE client_authentication_methods (
    client_id CHAR(36) NOT NULL,
    authentication_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    PRIMARY KEY(client_id, authentication_method),
    FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE,
    FOREIGN KEY (authentication_method) REFERENCES authentication_method (authentication_method)
);

CREATE TABLE authorization_grant_type (
    authorization_grant_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (authorization_grant_type)
);

CREATE TABLE client_authorization_grant_types (
    client_id CHAR(36) NOT NULL,
    authorization_grant_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE,
    FOREIGN KEY (authorization_grant_type) REFERENCES authorization_grant_type (authorization_grant_type),
    PRIMARY KEY (client_id, authorization_grant_type)
);

CREATE TABLE client_redirect_uris (
    client_id CHAR(36) NOT NULL,
    redirect_uri VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY(client_id) REFERENCES client (id) ON DELETE CASCADE,
    PRIMARY KEY (client_id, redirect_uri)
);

CREATE TABLE client_post_logout_redirect_uris (
    client_id CHAR(36) NOT NULL,
    post_logout_redirect_uri VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY(client_id) REFERENCES client (id) ON DELETE CASCADE,
    PRIMARY KEY (client_id, post_logout_redirect_uri)
);

CREATE TABLE client_scopes (
    client_id CHAR(36) NOT NULL,
    scope VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY(client_id) REFERENCES client (id) ON DELETE CASCADE,
    PRIMARY KEY (client_id, scope)
);

CREATE TABLE access_token (
    id CHAR(36) NOT NULL,
    access_token_value VARCHAR(4000) DEFAULT NULL,
    access_token_issued_at TIMESTAMP DEFAULT NULL,
    access_token_expires_at TIMESTAMP DEFAULT NULL,
    access_token_metadata VARCHAR(2000) DEFAULT NULL,
    access_token_type VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE refresh_token (
    id CHAR(36) NOT NULL,
    refresh_token_value VARCHAR(4000) DEFAULT NULL,
    refresh_token_issued_at TIMESTAMP DEFAULT NULL,
    refresh_token_expires_at TIMESTAMP DEFAULT NULL,
    refresh_token_metadata VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_code (
    id CHAR(36) NOT NULL,
    user_code_value VARCHAR(4000) DEFAULT NULL,
    user_code_issued_at TIMESTAMP DEFAULT NULL,
    user_code_expires_at TIMESTAMP DEFAULT NULL,
    user_code_metadata VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE device_code (
    id CHAR(36) NOT NULL,
    deviceCode_value VARCHAR(4000) DEFAULT NULL,
    device_code_issued_at TIMESTAMP DEFAULT NULL,
    device_code_expires_at TIMESTAMP DEFAULT NULL,
    device_code_metadata VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE authorization_code (
    id CHAR(36) NOT NULL,
    authorization_code_value VARCHAR(4000) DEFAULT NULL,
    authorization_code_issued_at TIMESTAMP DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP DEFAULT NULL,
    authorization_code_metadata VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oidc_id_token (
    id CHAR(36) NOT NULL,
    oidc_id_token_value VARCHAR(4000) DEFAULT NULL,
    oidc_id_token_issued_at TIMESTAMP DEFAULT NULL,
    oidc_id_token_expires_at TIMESTAMP DEFAULT NULL,
    oidc_id_token_metadata VARCHAR(2000) DEFAULT NULL,
    oidc_id_token_claims VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE client_settings (
    id CHAR(36) NOT NULL,
    require_proof_key INT CHECK (require_proof_key IN (0, 1)),
    require_authorization_consent INT CHECK (require_authorization_consent IN (0, 1)),
    jwk_set_url VARCHAR(255),
    token_endpoint_authentication_signing_algorithm VARCHAR(50),
    x509_certificate_subject_dn VARCHAR(255),
    FOREIGN KEY(id) REFERENCES client (id),
    FOREIGN KEY(token_endpoint_authentication_signing_algorithm) REFERENCES signing_algorithm(signing_algorithm),
    PRIMARY KEY (id)
);

CREATE TABLE client_token_settings (
    id CHAR(36) NOT NULL,
    authorization_code_time_to_live BIGINT,
    access_token_time_to_live BIGINT,
    access_token_format VARCHAR(255) CHECK (access_token_format IN ('self-contained', 'reference')),
    device_code_time_to_live BIGINT,
    reuse_refresh_tokens INT CHECK (reuse_refresh_tokens IN (0, 1)),
    refresh_token_time_to_live BIGINT,
    id_token_signature_algorithm VARCHAR(50),
    x509_certificate_bound_access_tokens INT CHECK (x509_certificate_bound_access_tokens IN (0, 1)),
    FOREIGN KEY(id) REFERENCES client (id),
    FOREIGN KEY(id_token_signature_algorithm) REFERENCES signing_algorithm(signing_algorithm),
    PRIMARY KEY (id)
);

CREATE TABLE "authorization" (
    id CHAR(36) NOT NULL,
    registered_client_id CHAR(36) NOT NULL,
    principal_name VARCHAR(255) NOT NULL,
    authorization_grant_type VARCHAR(50) NOT NULL,
    "state" VARCHAR(500) DEFAULT NULL,
    access_token_id CHAR(36) DEFAULT NULL,
    refresh_token_id CHAR(36) DEFAULT NULL,
    user_code_id CHAR(36) DEFAULT NULL,
    device_code_id CHAR(36) DEFAULT NULL,
    user_id CHAR(36) DEFAULT NULL,
    oidc_id_token_id CHAR(36) DEFAULT NULL,
    authorization_code_id CHAR(36) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    attributes VARCHAR(4000) DEFAULT NULL,

    FOREIGN KEY(registered_client_id) REFERENCES client (id),
    FOREIGN KEY(authorization_grant_type) REFERENCES authorization_grant_type (authorization_grant_type),
    FOREIGN KEY(access_token_id) REFERENCES access_token(id),
    FOREIGN KEY(refresh_token_id) REFERENCES refresh_token(id),
    FOREIGN KEY(user_code_id) REFERENCES user_code(id),
    FOREIGN KEY(device_code_id) REFERENCES device_code(id),
    FOREIGN KEY(user_id) REFERENCES "user"(id),
    FOREIGN KEY(authorization_code_id) REFERENCES authorization_code(id),
    FOREIGN KEY(oidc_id_token_id) REFERENCES oidc_id_token(id),
    PRIMARY KEY (id)
);

CREATE TABLE access_token_scope (
    id CHAR(36) NOT NULL,
    access_token_scope VARCHAR(255),
    FOREIGN KEY(id) REFERENCES access_token(id),
    PRIMARY KEY(id, access_token_scope)
);

CREATE TABLE authorization_scope (
    authorization_id CHAR(36) NOT NULL,
    scope VARCHAR(255) NOT NULL,
    FOREIGN KEY(authorization_id) REFERENCES "authorization"(id),
    PRIMARY KEY(authorization_id, scope)
);

CREATE TABLE authorization_request (
    id CHAR(36) NOT NULL,
    authorization_uri VARCHAR(255) NOT NULL,
    authorization_grant_type VARCHAR(50) NOT NULL,
    response_type VARCHAR(255) CHECK (response_type IN ('code')),
    client_id VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(255) NOT NULL,
    state VARCHAR(255),
    authorization_request_uri VARCHAR(255),
    FOREIGN KEY(authorization_grant_type) REFERENCES authorization_grant_type (authorization_grant_type),
    FOREIGN KEY(id) REFERENCES "authorization"(id),
    PRIMARY KEY(id)
);

CREATE TABLE authorization_request_scope (
    authorization_request_id CHAR(36) NOT NULL,
    authorization_request_scope VARCHAR(255),
    FOREIGN KEY(authorization_request_id) REFERENCES authorization_request(id),
    PRIMARY KEY(authorization_request_id, authorization_request_scope)
);

CREATE TABLE authorization_server_settings (
    id INT DEFAULT 1 CHECK (id = 1),
    issuer VARCHAR(255) NOT NULL,
    authorization_endpoint VARCHAR(255) NOT NULL,
    device_authorization_endpoint VARCHAR(255) NOT NULL,
    device_verification_endpoint VARCHAR(255) NOT NULL,
    token_endpoint VARCHAR(255) NOT NULL,
    token_introspection_endpoint VARCHAR(255) NOT NULL,
    token_revocation_endpoint VARCHAR(255) NOT NULL,
    jwk_set_endpoint VARCHAR(255) NOT NULL,
    oidc_logout_endpoint VARCHAR(255) NOT NULL,
    oidc_user_info_endpoint VARCHAR(255) NOT NULL,
    oidc_client_registration_endpoint VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO authorization_grant_type (authorization_grant_type) VALUES
    ('authorization_code'),
    ('refresh_token'),
    ('client_credentials'),
    ('jwt_bearer'),
    ('device_code'),
    ('token_exchange');

INSERT INTO authentication_method (authentication_method) VALUES
    ('client_secret_basic'),
    ('client_secret_post'),
    ('client_secret_jwt'),
    ('private_key_jwt'),
    ('none'),
    ('tls_client_auth'),
    ('self_signed_tls_client_auth');

INSERT INTO signing_algorithm (signing_algorithm) VALUES
    ('RS256'),
    ('RS384'),
    ('RS512'),
    ('PS256'),
    ('PS384'),
    ('PS512'),
    ('ES256'),
    ('ES384'),
    ('ES512'),
    ('HS256'),
    ('HS384'),
    ('HS512');