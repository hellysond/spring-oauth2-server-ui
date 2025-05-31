package com.hellysond.spring.oauth2.server.ui.configuration.flyway;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.*;

@Component
public class V0_0_2_INSERT_INITIAL_CONFIGURATION implements JavaMigration {

    PasswordEncoder passwordEncoder;

    Set<AuthorizationGrantType> authorizationGrantTypes = Set.of(AUTHORIZATION_CODE,REFRESH_TOKEN,
            CLIENT_CREDENTIALS,PASSWORD,JWT_BEARER,DEVICE_CODE,TOKEN_EXCHANGE);

    Set<ClientAuthenticationMethod> authenticationMethods = Set.of(CLIENT_SECRET_BASIC,
            CLIENT_SECRET_POST,CLIENT_SECRET_JWT,PRIVATE_KEY_JWT,NONE,TLS_CLIENT_AUTH,SELF_SIGNED_TLS_CLIENT_AUTH);

    SignatureAlgorithm[] signiningAlgorithms = SignatureAlgorithm.values();

    public V0_0_2_INSERT_INITIAL_CONFIGURATION(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion("0.0.2");
    }

    @Override
    public String getDescription() {
        return "init insert configuration data";
    }

    @Override
    public Integer getChecksum() {
        return 0;
    }

    @Override
    public boolean canExecuteInTransaction() {
        return false;
    }

    @Override
    public void migrate(Context context) throws Exception {

        try (Connection connection = context.getConnection()) {

            String insertSigningAlgorithmSql = "insert into public.signing_algorithm (id, signing_algorithm) values (?,?)";

            Map<String,UUID> mapSigningAlgorithmToUUID = mapSigningAlgorithmToUUID();

            try(PreparedStatement insertSigningAlgorithm = connection.prepareStatement(insertSigningAlgorithmSql)){

                for(Map.Entry<String, UUID> entry : mapSigningAlgorithmToUUID.entrySet()){
                    insertSigningAlgorithm.setString(1, String.valueOf(entry.getValue()));
                    insertSigningAlgorithm.setString(2,entry.getKey());
                    insertSigningAlgorithm.addBatch();
                }
                insertSigningAlgorithm.executeBatch();
            }

            String insertAdminClientSql = "insert into " +
                    "client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name) " +
                    "values (?,?,?,?,?,?)";


            String clientId = UUID.randomUUID().toString();

            try (PreparedStatement insertAdminClient = connection.prepareStatement(insertAdminClientSql)) {

                insertAdminClient.setString(1, clientId);
                insertAdminClient.setString(2, "spring-oauth2-server-ui");
                insertAdminClient.setTimestamp(3, Timestamp.from(Instant.now()));
                insertAdminClient.setString(4, passwordEncoder.encode("secret"));
                insertAdminClient.setTimestamp(5,getAdminClientSecretExpirationDate());
                insertAdminClient.setString(6, "spring-oauth2-server-ui" );

                insertAdminClient.executeUpdate();
            }

            String insertAuthorizationGrantTypeSql = "insert into authorization_grant_type (id, authorization_grant_type) " +
                    "values (?,?)";

            Map<String,UUID> mapAuthorizationGrantType = mapAuthorizationGrantTypeToUUID(authorizationGrantTypes);

            try (PreparedStatement insertAuthorizationGrantType = connection.prepareStatement(insertAuthorizationGrantTypeSql)) {

                for (Map.Entry<String, UUID> entry : mapAuthorizationGrantType.entrySet()){
                    insertAuthorizationGrantType.setString(1,String.valueOf(entry.getValue()));
                    insertAuthorizationGrantType.setString(2,entry.getKey());
                    insertAuthorizationGrantType.addBatch();
                }
                insertAuthorizationGrantType.executeBatch();
            }

            String insertAuthenticationMethodSql = "insert into authentication_method (id, authentication_method) " +
                    "values (?,?)";

            Map<String,UUID> mapAuthenticationMethods = mapAuthenticationMethodsToUUID(authenticationMethods);

            try (PreparedStatement insertAuthenticationMethod = connection.prepareStatement(insertAuthenticationMethodSql)) {

                for (Map.Entry<String, UUID> entry : mapAuthenticationMethods.entrySet()){
                    insertAuthenticationMethod.setString(1,String.valueOf(entry.getValue()));
                    insertAuthenticationMethod.setString(2,entry.getKey() );

                    insertAuthenticationMethod.addBatch();
                }
                insertAuthenticationMethod.executeBatch();
            }

            String insertAdminClientAuthenticationMethodSql = "insert into client_authentication_methods (client_id, authentication_method_id)" +
                    " values (?,?)";

            try (PreparedStatement insertClientAuthenticationMethod = connection.prepareStatement(insertAdminClientAuthenticationMethodSql)) {

                insertClientAuthenticationMethod.setString(1,clientId);
                insertClientAuthenticationMethod.setString(2, String.valueOf(mapAuthenticationMethods.get(CLIENT_SECRET_BASIC.getValue())));
                insertClientAuthenticationMethod.executeUpdate();
            }

            String insertAdminClientGrantTypeSql = "insert into client_authorization_grant_types (client_id, authorization_grant_type_id)" +
                    " values(?,?) ";

            try (PreparedStatement insertAdminClientGrantType = connection.prepareStatement(insertAdminClientGrantTypeSql)) {

                insertAdminClientGrantType.setString(1,clientId);
                insertAdminClientGrantType.setString(2, String.valueOf(mapAuthorizationGrantType.get(AUTHORIZATION_CODE.getValue())));
                insertAdminClientGrantType.executeUpdate();
            }

            String insertAdminUserSql = "insert into \"user\" (id, username, password, enabled, email, created_at) values (?,?,?,?,?,?)";

            try (PreparedStatement insertAdminUser = connection.prepareStatement(insertAdminUserSql)) {

                insertAdminUser.setString(1,UUID.randomUUID().toString());
                insertAdminUser.setString(2, "admin");
                insertAdminUser.setString(3, passwordEncoder.encode("password"));
                insertAdminUser.setInt(4,1);
                insertAdminUser.setString(5,"hellysonjk2022@gmail.com");
                insertAdminUser.setTimestamp(6, Timestamp.from(Instant.now()));
                insertAdminUser.executeUpdate();
            }

            String insertClientScopesSql = "insert into client_scopes (client_id, scope) values(?,?)";

            try (PreparedStatement insertClientScopes = connection.prepareStatement(insertClientScopesSql)) {

                insertClientScopes.setString(1,clientId);
                insertClientScopes.setString(2, "read");
                insertClientScopes.addBatch();

                insertClientScopes.setString(1,clientId);
                insertClientScopes.setString(2, "openid");
                insertClientScopes.addBatch();

                insertClientScopes.executeBatch();

            }

            String insertClientRedirectUris = "insert into client_redirect_uris (client_id, redirect_uri) values(?,?)";

            try (PreparedStatement insertClientRedirectUri = connection.prepareStatement(insertClientRedirectUris)) {
                insertClientRedirectUri.setString(1,clientId);
                insertClientRedirectUri.setString(2, "http://127.0.0.1:7860".trim());
                insertClientRedirectUri.executeUpdate();
            }

            String insertClientTokenSettingsSql = "insert into client_token_settings (id, authorization_code_time_to_live, access_token_time_to_live, access_token_format," +
                "device_code_time_to_live, reuse_refresh_tokens, refresh_token_time_to_live, id_token_signature_algorithm, x509_certificate_bound_access_tokens) " +
                    "values(?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement insertClientTokenSettings = connection.prepareStatement(insertClientTokenSettingsSql)) {

                insertClientTokenSettings.setString(1,clientId);
                insertClientTokenSettings.setLong(2,  Duration.ofMinutes(5).toNanos());
                insertClientTokenSettings.setLong(3,Duration.ofMinutes(5).toNanos());
                insertClientTokenSettings.setString(4, OAuth2TokenFormat.SELF_CONTAINED.getValue());
                insertClientTokenSettings.setLong(5,Duration.ofMinutes(5).toNanos());
                insertClientTokenSettings.setInt(6,1);
                insertClientTokenSettings.setLong(7,Duration.ofMinutes(5).toNanos());
                insertClientTokenSettings.setString(8, String.valueOf(mapSigningAlgorithmToUUID.get(SignatureAlgorithm.RS256.getName())));
                insertClientTokenSettings.setInt(9, 0);
                insertClientTokenSettings.executeUpdate();
            }

            String insertClientSettingsSql = "insert into client_settings (id, require_proof_key, require_authorization_consent, jwk_set_url, " +
                "token_endpoint_authentication_signing_algorithm, x509_certificate_subject_dn) values(?,?,?,?,?,?)";

            try (PreparedStatement insertClientSettings = connection.prepareStatement(insertClientSettingsSql)) {

                insertClientSettings.setString(1,clientId);
                insertClientSettings.setInt(2, 0);
                insertClientSettings.setInt(3, 0);
                insertClientSettings.setString(4, null);
                insertClientSettings.setString(5, String.valueOf(mapSigningAlgorithmToUUID.get(SignatureAlgorithm.RS256.getName())));
                insertClientSettings.setString(6, null);
                insertClientSettings.executeUpdate();

            }

        }

    }

    private Map<String,UUID> mapSigningAlgorithmToUUID(){
        return Arrays.stream(signiningAlgorithms).collect(Collectors.toMap(SignatureAlgorithm::getName,e->UUID.randomUUID()));
    }

    private Map<String,UUID> mapAuthorizationGrantTypeToUUID(Set<AuthorizationGrantType> authorizationGrantTypes){
        return authorizationGrantTypes.stream().collect(Collectors.toMap(AuthorizationGrantType::getValue,e->UUID.randomUUID()));
    }

    private Map<String,UUID> mapAuthenticationMethodsToUUID(Set<ClientAuthenticationMethod> authenticationMethods){
        return authenticationMethods.stream().collect(Collectors.toMap(ClientAuthenticationMethod::getValue,e->UUID.randomUUID()));
    }

    private Timestamp getAdminClientSecretExpirationDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(9999, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
}
