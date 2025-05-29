package com.hellysond.spring.oauth2.server.ui.service.authorization;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hellysond.spring.oauth2.server.ui.model.entity.AuthorizationEntity;
import com.hellysond.spring.oauth2.server.ui.model.entity.AuthorizationGrantTypeEntity;
import com.hellysond.spring.oauth2.server.ui.model.entity.ClientEntity;
import com.hellysond.spring.oauth2.server.ui.repository.authorization.AuthorizationEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.authorization.AuthorizationGrantTypeEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.client.ClientEntityRepository;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {
	private final AuthorizationEntityRepository authorizationEntityRepository;
	private final RegisteredClientRepository registeredClientRepository;
	private final ClientEntityRepository clientEntityRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final AuthorizationGrantTypeEntityRepository authorizationGrantTypeEntityRepository;

	public JpaOAuth2AuthorizationService(AuthorizationEntityRepository authorizationEntityRepository, RegisteredClientRepository registeredClientRepository, ClientEntityRepository clientEntityRepository, AuthorizationGrantTypeEntityRepository authorizationGrantTypeEntityRepository) {
        this.clientEntityRepository = clientEntityRepository;
        this.authorizationGrantTypeEntityRepository = authorizationGrantTypeEntityRepository;
		this.authorizationEntityRepository = authorizationEntityRepository;
		this.registeredClientRepository = registeredClientRepository;

		ClassLoader classLoader = JpaOAuth2AuthorizationService.class.getClassLoader();
		List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
		this.objectMapper.registerModules(securityModules);
		this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
	}

	@Override
	public void save(OAuth2Authorization authorization) {
		Assert.notNull(authorization, "authorization cannot be null");
		this.authorizationEntityRepository.save(toEntity(authorization));
	}

	@Override
	public void remove(OAuth2Authorization authorization) {
		Assert.notNull(authorization, "authorization cannot be null");
		this.authorizationEntityRepository.deleteById(authorization.getId());
	}

	@Override
	public OAuth2Authorization findById(String id) {
		Assert.hasText(id, "id cannot be empty");
		return this.authorizationEntityRepository.findById(id).map(this::toObject).orElse(null);
	}

	@Override
	public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
		Assert.hasText(token, "token cannot be empty");

		Optional<AuthorizationEntity> result;
		if (tokenType == null) {
			result = this.authorizationEntityRepository.findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(token);
		} else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByState(token);
		} else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByAuthorizationCodeEntity_AuthorizationCodeValue(token);
		} else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByAccessTokenEntity_AccessTokenValue(token);
		} else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByRefreshTokenEntity_RefreshTokenValue(token);
		} else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByOidcIdTokenEntity_OidcIdTokenValue(token);
		} else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByUserCode_UserCodeValue(token);
		} else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
			result = this.authorizationEntityRepository.findByDeviceCodeEntity_DeviceCodeValue(token);
		} else {
			result = Optional.empty();
		}

		return result.map(this::toObject).orElse(null);
	}

	private OAuth2Authorization toObject(AuthorizationEntity entity) {
		RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getId().toString());


		OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
				.id(entity.getId().toString())
				.principalName(entity.getPrincipalName())
				.authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantTypeEntity().getAuthorizationGrantType()))
				.authorizedScopes(entity.getScopes());
				//.attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));

		if (entity.getState() != null) {
			builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
		}

		if (entity.getAuthorizationCodeEntity() != null) {
			OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
					entity.getAuthorizationCodeEntity() .getAuthorizationCodeValue(),
					entity.getAuthorizationCodeEntity().getAuthorizationCodeIssuedAt(),
					entity.getAuthorizationCodeEntity().getAuthorizationCodeExpiresAt());
			builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(null)));
		}

		if (entity.getAccessTokenEntity() != null) {
			OAuth2AccessToken accessToken = new OAuth2AccessToken(
					OAuth2AccessToken.TokenType.BEARER,
					entity.getAccessTokenEntity().getAccessTokenValue(),
					entity.getAccessTokenEntity().getAccessTokenIssuedAt(),
					entity.getAccessTokenEntity().getAccessTokenExpiresAt(),
					entity.getAccessTokenEntity().getScopes());
			builder.token(accessToken, metadata -> metadata.putAll(parseMap(null)));
		}

		if (entity.getRefreshTokenEntity() != null) {
			OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
					entity.getRefreshTokenEntity().getRefreshTokenValue(),
					entity.getRefreshTokenEntity().getRefreshTokenIssuedAt(),
					entity.getRefreshTokenEntity().getRefreshTokenExpiresAt());
			builder.token(refreshToken, metadata -> metadata.putAll((parseMap(null))));
		}

		if (entity.getOidcIdTokenEntity() != null) {
			OidcIdToken idToken = new OidcIdToken(
					entity.getOidcIdTokenEntity().getOidcIdTokenValue(),
					entity.getOidcIdTokenEntity().getOidcIdTokenIssuedAt(),
					entity.getOidcIdTokenEntity().getOidcIdTokenExpiresAt(),
					parseMap(entity.getOidcIdTokenEntity().getOidcIdTokenClaims()));
			builder.token(idToken, metadata -> metadata.putAll(parseMap(null)));
		}

		if (entity.getUserCode() != null) {
			OAuth2UserCode userCode = new OAuth2UserCode(
					entity.getUserCode().getUserCodeValue(),
					entity.getUserCode().getUserCodeIssuedAt(),
					entity.getUserCode().getUserCodeExpiresAt());
			builder.token(userCode, metadata -> metadata.putAll(parseMap(null)));
		}

		if (entity.getDeviceCodeEntity() != null) {
			OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(
					entity.getDeviceCodeEntity().getDeviceCodeValue(),
					entity.getDeviceCodeEntity().getDeviceCodeIssuedAt(),
					entity.getDeviceCodeEntity().getDeviceCodeExpiresAt());
			builder.token(deviceCode, metadata -> metadata.putAll(parseMap(null)));
		}

		return builder.build();
	}

	private AuthorizationEntity toEntity(OAuth2Authorization authorization) {
		AuthorizationEntity entity = new AuthorizationEntity();
		entity.setRegisteredClientEntity(resolveClientEntity(authorization.getRegisteredClientId()));
		entity.setPrincipalName(authorization.getPrincipalName());
		entity.setAuthorizationGrantTypeEntity(resolveAuthorizationGrantTypeEntity(authorization.getAuthorizationGrantType().getValue()));
		entity.setScopes(authorization.getAuthorizedScopes());
		//entity.setAttributes(writeMap(authorization.getAttributes()));
		entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

		OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
				authorization.getToken(OAuth2AuthorizationCode.class);


		var authorizationCodeEntity = entity.getAuthorizationCodeEntity();

		setTokenValues(
				authorizationCode,
				authorizationCodeEntity::setAuthorizationCodeValue,
				authorizationCodeEntity::setAuthorizationCodeIssuedAt,
				authorizationCodeEntity::setAuthorizationCodeExpiresAt,
				authorizationCodeEntity::setAuthorizationCodeMetadata
		);

		var accessTokenEntity = entity.getAccessTokenEntity();

		OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
				authorization.getToken(OAuth2AccessToken.class);
		setTokenValues(
				accessToken,
				accessTokenEntity::setAccessTokenValue,
				accessTokenEntity::setAccessTokenIssuedAt,
				accessTokenEntity::setAccessTokenExpiresAt,
				accessTokenEntity::setAccessTokenMetadata
		);
		if (accessToken != null && accessToken.getToken().getScopes() != null) {
			accessTokenEntity.setScopes(accessToken.getToken().getScopes());
		}

		OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
				authorization.getToken(OAuth2RefreshToken.class);

		var refreshTokenEntity = entity.getRefreshTokenEntity();

		setTokenValues(
				refreshToken,
				refreshTokenEntity::setRefreshTokenValue,
				refreshTokenEntity::setRefreshTokenIssuedAt,
				refreshTokenEntity::setRefreshTokenExpiresAt,
				refreshTokenEntity::setRefreshTokenMetadata
		);

		OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
				authorization.getToken(OidcIdToken.class);

		var oidcIdTokenEntity = entity.getOidcIdTokenEntity();

		setTokenValues(
				oidcIdToken,
				oidcIdTokenEntity::setOidcIdTokenValue,
				oidcIdTokenEntity::setOidcIdTokenIssuedAt,
				oidcIdTokenEntity::setOidcIdTokenExpiresAt,
				oidcIdTokenEntity::setOidcIdTokenMetadata
		);
		if (oidcIdToken != null) {
			oidcIdTokenEntity.setOidcIdTokenClaims(writeMap(oidcIdToken.getClaims()));
		}

		OAuth2Authorization.Token<OAuth2UserCode> userCode =
				authorization.getToken(OAuth2UserCode.class);

		var userCodeEntity = entity.getUserCode();

		setTokenValues(
				userCode,
				userCodeEntity::setUserCodeValue,
				userCodeEntity::setUserCodeIssuedAt,
				userCodeEntity::setUserCodeExpiresAt,
				userCodeEntity::setUserCodeMetadata
		);

		OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode =
				authorization.getToken(OAuth2DeviceCode.class);

		var deviceCodeEntity = entity.getDeviceCodeEntity();

		setTokenValues(
				deviceCode,
				deviceCodeEntity::setDeviceCodeValue,
				deviceCodeEntity::setDeviceCodeIssuedAt,
				deviceCodeEntity::setDeviceCodeExpiresAt,
				deviceCodeEntity::setDeviceCodeMetadata
		);

		return entity;
	}

	private void setTokenValues(
			OAuth2Authorization.Token<?> token,
			Consumer<String> tokenValueConsumer,
			Consumer<Instant> issuedAtConsumer,
			Consumer<Instant> expiresAtConsumer,
			Consumer<String> metadataConsumer) {
		if (token != null) {
			OAuth2Token oAuth2Token = token.getToken();
			tokenValueConsumer.accept(oAuth2Token.getTokenValue());
			issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
			expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
			metadataConsumer.accept(writeMap(token.getMetadata()));
		}
	}

	private Map<String, Object> parseMap(String data) {
		try {
			return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	private String writeMap(Map<String, Object> metadata) {
		try {
			return this.objectMapper.writeValueAsString(metadata);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
		if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
			return AuthorizationGrantType.AUTHORIZATION_CODE;
		} else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
			return AuthorizationGrantType.CLIENT_CREDENTIALS;
		} else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
			return AuthorizationGrantType.REFRESH_TOKEN;
		} else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
			return AuthorizationGrantType.DEVICE_CODE;
		}
		return new AuthorizationGrantType(authorizationGrantType);              // Custom authorization grant type
	}

	public ClientEntity resolveClientEntity(String clientId){
		return clientEntityRepository.findByClientId(clientId).orElseThrow();
	}

	public AuthorizationGrantTypeEntity resolveAuthorizationGrantTypeEntity(String value){
		return authorizationGrantTypeEntityRepository.findByAuthorizationGrantType(value).orElseThrow();
	}
}
