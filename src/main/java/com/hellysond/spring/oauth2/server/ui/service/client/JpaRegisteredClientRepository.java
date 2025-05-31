package com.hellysond.spring.oauth2.server.ui.service.client;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hellysond.spring.oauth2.server.ui.model.entity.*;
import com.hellysond.spring.oauth2.server.ui.repository.client.AuthenticationMethodEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.authorization.AuthorizationGrantTypeEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.client.ClientEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.client.SigningAlgorithmEntityRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class JpaRegisteredClientRepository implements RegisteredClientRepository {
	private final ClientEntityRepository clientEntityRepository;
	private final AuthorizationGrantTypeEntityRepository authorizationGrantTypeEntityRepository;
	private final AuthenticationMethodEntityRepository authenticationMethodEntityRepository;
	private final SigningAlgorithmEntityRepository signingAlgorithmEntityRepository;

	public JpaRegisteredClientRepository(ClientEntityRepository clientEntityRepository, AuthorizationGrantTypeEntityRepository authorizationGrantTypeEntityRepository, AuthenticationMethodEntityRepository authenticationMethodEntityRepository, SigningAlgorithmEntityRepository signingAlgorithmEntityRepository) {
        this.authenticationMethodEntityRepository = authenticationMethodEntityRepository;
		this.clientEntityRepository = clientEntityRepository;
		this.authorizationGrantTypeEntityRepository = authorizationGrantTypeEntityRepository;
        this.signingAlgorithmEntityRepository = signingAlgorithmEntityRepository;
	}

	@Override
	public void save(RegisteredClient registeredClient) {
		Assert.notNull(registeredClient, "registeredClient cannot be null");
		this.clientEntityRepository.save(toEntity(registeredClient));
	}

	@Override
	public RegisteredClient findById(String id) {
		Assert.hasText(id, "id cannot be empty");
		return this.clientEntityRepository.findById(UUID.fromString(id)).map(this::toObject).orElse(null);
	}

	@Override
	@Transactional
	public RegisteredClient findByClientId(String clientId) {
		Assert.hasText(clientId, "clientId cannot be empty");
		return this.clientEntityRepository.findByClientId(clientId).map(this::toObject).orElse(null);
	}

	private RegisteredClient toObject(ClientEntity clientEntity) {

		RegisteredClient.Builder builder = RegisteredClient.withId(clientEntity.getId().toString())
				.clientId(clientEntity.getClientId())
				.clientIdIssuedAt(clientEntity.getClientIdIssuedAt())
				.clientSecret(clientEntity.getClientSecret())
				.clientSecretExpiresAt(clientEntity.getClientSecretExpiresAt())
				.clientName(clientEntity.getClientName())
				.clientAuthenticationMethods(authenticationMethods -> clientEntity.getAuthenticationMethodEntities().forEach(authenticationMethod ->
								authenticationMethods.add(new ClientAuthenticationMethod(authenticationMethod.getAuthenticationMethod()))))
				.authorizationGrantTypes((grantTypes) -> clientEntity.getAuthorizationGrantTypeEntities().forEach(grantType ->
								grantTypes.add(new AuthorizationGrantType(grantType.getAuthorizationGrantType()))))
				.redirectUris((uris) -> uris.addAll(clientEntity.getRedirectUris()))
				.postLogoutRedirectUris((uris) -> uris.addAll(clientEntity.getPostLogoutRedirectUris()))
				.scopes((scopes) -> scopes.addAll(clientEntity.getScopes()))
				.clientSettings(toObject(clientEntity.getSettings()))
				.tokenSettings(toObject(clientEntity.getTokenSettings()));


		return builder.build();
	}

	private ClientEntity toEntity(RegisteredClient registeredClient) {
		ClientEntity entity = new ClientEntity();
		entity.setId(UUID.fromString(registeredClient.getId()));
		entity.setClientId(registeredClient.getClientId());
		entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
		entity.setClientSecret(registeredClient.getClientSecret());
		entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
		entity.setClientName(registeredClient.getClientName());
		entity.setAuthenticationMethodEntities(resolveAuthenticationMethod(registeredClient.getClientAuthenticationMethods()));
		entity.setAuthorizationGrantTypeEntities(resolveAuthorizationGrantType(registeredClient.getAuthorizationGrantTypes()));
		entity.setRedirectUris(registeredClient.getRedirectUris());
		entity.setPostLogoutRedirectUris(registeredClient.getPostLogoutRedirectUris());
		entity.setScopes(registeredClient.getScopes());
		entity.setSettings(toEntity(registeredClient.getClientSettings()));
		entity.setTokenSettings(toEntity(registeredClient.getTokenSettings()));
		return entity;
	}

	private ClientTokenSettingsEntity toEntity(TokenSettings tokenSettings){
		ClientTokenSettingsEntity clientTokenSettingsEntity = new ClientTokenSettingsEntity();
		clientTokenSettingsEntity.setAccessTokenFormat(tokenSettings.getAccessTokenFormat().getValue());
		clientTokenSettingsEntity.setAccessTokenTimeToLive(tokenSettings.getAccessTokenTimeToLive());
		clientTokenSettingsEntity.setAuthorizationCodeTimeToLive(tokenSettings.getAuthorizationCodeTimeToLive());
		clientTokenSettingsEntity.setDeviceCodeTimeToLive(tokenSettings.getDeviceCodeTimeToLive());
		clientTokenSettingsEntity.setRefreshTokenTimeToLive(tokenSettings.getDeviceCodeTimeToLive());
		clientTokenSettingsEntity.setReuseRefreshTokens(tokenSettings.isReuseRefreshTokens());
		clientTokenSettingsEntity.setX509CertificateBoundAccessTokens(tokenSettings.isX509CertificateBoundAccessTokens());
		clientTokenSettingsEntity.setIdTokenSignatureAlgorithm(resolveSigningAlgorithmEntity(tokenSettings.getIdTokenSignatureAlgorithm().getName()));
		return clientTokenSettingsEntity;
	}

	private ClientSettingsEntity toEntity(ClientSettings clientSettings){
		ClientSettingsEntity clientSettingsEntity = new ClientSettingsEntity();
		clientSettingsEntity.setJwkSetUrl(clientSettings.getJwkSetUrl());
		clientSettingsEntity.setRequireAuthorizationConsent(clientSettings.isRequireAuthorizationConsent());
		clientSettingsEntity.setRequireProofKey(clientSettings.isRequireProofKey());
		clientSettingsEntity.setTokenEndpointAuthenticationSigningAlgorithmEntity(resolveSigningAlgorithmEntity(clientSettings.getTokenEndpointAuthenticationSigningAlgorithm().getName()));
		clientSettingsEntity.setX509CertificateSubjectDn(clientSettings.getX509CertificateSubjectDN());
		return clientSettingsEntity;
	}

	TokenSettings toObject(ClientTokenSettingsEntity clientTokenSettingsEntity){
		return TokenSettings.builder()
				.deviceCodeTimeToLive(clientTokenSettingsEntity.getDeviceCodeTimeToLive())
				.idTokenSignatureAlgorithm(SignatureAlgorithm.from(clientTokenSettingsEntity.getIdTokenSignatureAlgorithm().getSigningAlgorithm()))
				.refreshTokenTimeToLive(clientTokenSettingsEntity.getRefreshTokenTimeToLive())
				.reuseRefreshTokens(clientTokenSettingsEntity.isReuseRefreshTokens())
				.x509CertificateBoundAccessTokens(clientTokenSettingsEntity.isX509CertificateBoundAccessTokens())
			.build();
	}

	ClientSettings toObject(ClientSettingsEntity clientSettingsEntity){
		ClientSettings.Builder clientSettings = ClientSettings.builder()
				.requireAuthorizationConsent(clientSettingsEntity.isRequireAuthorizationConsent())
				.tokenEndpointAuthenticationSigningAlgorithm(resolveJwsAlgorithm(clientSettingsEntity
				.getTokenEndpointAuthenticationSigningAlgorithmEntity().getSigningAlgorithm()))
				.requireProofKey(clientSettingsEntity.isRequireProofKey());

		if(clientSettingsEntity.getX509CertificateSubjectDn()!=null)
			clientSettings.x509CertificateSubjectDN(clientSettingsEntity.getX509CertificateSubjectDn());

		if(clientSettingsEntity.getJwkSetUrl()!=null)
			clientSettings.jwkSetUrl(clientSettingsEntity.getJwkSetUrl());

		return clientSettings.build();

	}

	Set<AuthorizationGrantTypeEntity> resolveAuthorizationGrantType(Set<AuthorizationGrantType> authorizationGrantTypes){
		return authorizationGrantTypes.stream().map(authorizationGrantType -> authorizationGrantTypeEntityRepository
				.findByAuthorizationGrantType(authorizationGrantType.getValue())
			.orElseThrow(RuntimeException::new)).collect(Collectors.toSet());
	}

	Set<AuthenticationMethodEntity> resolveAuthenticationMethod(Set<ClientAuthenticationMethod> authenticationMethods){
		return authenticationMethods.stream().map(authenticationMethod -> authenticationMethodEntityRepository
				.findByAuthenticationMethod(authenticationMethod.getValue())
				.orElseThrow(RuntimeException::new)).collect(Collectors.toSet());
	}

	SigningAlgorithmEntity resolveSigningAlgorithmEntity(String value){
		return signingAlgorithmEntityRepository.findBySigningAlgorithm(value).orElseThrow();
	}

	private JwsAlgorithm resolveJwsAlgorithm(String name){
		MacAlgorithm macAlgorithm = MacAlgorithm.from(name);
		if(macAlgorithm==null){
            return SignatureAlgorithm.from(name);
		}else{
			return macAlgorithm;
		}
	}

}
