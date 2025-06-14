package com.hellysond.spring.oauth2.server.ui.service.authorization;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hellysond.spring.oauth2.server.ui.model.entity.*;
import com.hellysond.spring.oauth2.server.ui.repository.authorization.AuthorizationEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.authorization.AuthorizationGrantTypeEntityRepository;
import com.hellysond.spring.oauth2.server.ui.repository.client.ClientEntityRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForBigDecimal;
import org.springframework.security.config.annotation.web.oauth2.client.AuthorizationCodeGrantDsl;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
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
import org.springframework.transaction.annotation.Transactional;
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

	@Transactional
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
		RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientEntity().getId().toString());

		OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
				.id(entity.getId().toString())
				.principalName(entity.getPrincipalName())
				.authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantTypeEntity().getAuthorizationGrantType()))
				.authorizedScopes(entity.getScopes());

		if (entity.getState() != null) {
			builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
		}

		if (entity.getAuthorizationCodeEntity() != null) {
			OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
					entity.getAuthorizationCodeEntity() .getAuthorizationCodeValue(),
					entity.getAuthorizationCodeEntity().getAuthorizationCodeIssuedAt(),
					entity.getAuthorizationCodeEntity().getAuthorizationCodeExpiresAt());

			builder.token(authorizationCode, metadata -> metadata.putAll(new HashMap<>()));
		}

		if (entity.getAccessTokenEntity() != null) {
			OAuth2AccessToken accessToken = new OAuth2AccessToken(
					OAuth2AccessToken.TokenType.BEARER,
					entity.getAccessTokenEntity().getAccessTokenValue(),
					entity.getAccessTokenEntity().getAccessTokenIssuedAt(),
					entity.getAccessTokenEntity().getAccessTokenExpiresAt(),
					entity.getAccessTokenEntity().getScopes());
			builder.token(accessToken, metadata -> metadata.putAll(new HashMap<>()));
		}

		if (entity.getRefreshTokenEntity() != null) {
			OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
					entity.getRefreshTokenEntity().getRefreshTokenValue(),
					entity.getRefreshTokenEntity().getRefreshTokenIssuedAt(),
					entity.getRefreshTokenEntity().getRefreshTokenExpiresAt());
			builder.token(refreshToken, metadata -> metadata.putAll(new HashMap<>()));
		}

		if (entity.getOidcIdTokenEntity() != null) {
			OidcIdToken idToken = new OidcIdToken(
					entity.getOidcIdTokenEntity().getOidcIdTokenValue(),
					entity.getOidcIdTokenEntity().getOidcIdTokenIssuedAt(),
					entity.getOidcIdTokenEntity().getOidcIdTokenExpiresAt(),
					null);
			builder.token(idToken, metadata -> metadata.putAll(new HashMap<>()));
		}

		if (entity.getUserCode() != null) {
			OAuth2UserCode userCode = new OAuth2UserCode(
					entity.getUserCode().getUserCodeValue(),
					entity.getUserCode().getUserCodeIssuedAt(),
					entity.getUserCode().getUserCodeExpiresAt());
			builder.token(userCode, metadata -> metadata.putAll(new HashMap<>()));
		}

		if (entity.getDeviceCodeEntity() != null) {
			OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(
					entity.getDeviceCodeEntity().getDeviceCodeValue(),
					entity.getDeviceCodeEntity().getDeviceCodeIssuedAt(),
					entity.getDeviceCodeEntity().getDeviceCodeExpiresAt());
			builder.token(deviceCode, metadata -> metadata.putAll(new HashMap<>()));
		}

		Map<String,Object> attributes = new HashMap<>();
		attributes.put(OAuth2AuthorizationRequest.class.getName(),toObject(entity.getAuthorizationRequestEntity()));

		builder.attributes(m->m.putAll(attributes));

		return builder.build();
	}

	private AuthorizationEntity toEntity(OAuth2Authorization authorization) {
		AuthorizationEntity entity = new AuthorizationEntity();
		entity.setRegisteredClientEntity(resolveClientEntity(authorization.getRegisteredClientId()));
		entity.setPrincipalName(authorization.getPrincipalName());
		entity.setAuthorizationGrantTypeEntity(resolveAuthorizationGrantTypeEntity(authorization.getAuthorizationGrantType().getValue()));
		entity.setScopes(authorization.getAuthorizedScopes());
		entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));
		entity.setAuthorizationRequestEntity(toEntity((OAuth2AuthorizationRequest) authorization.getAttribute(OAuth2AuthorizationRequest.class.getName())));


		OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
				authorization.getToken(OAuth2AuthorizationCode.class);

		if(authorizationCode!=null){
			AuthorizationCodeEntity authorizationCodeEntity = new AuthorizationCodeEntity();

			authorizationCodeEntity.setAuthorizationCodeValue(authorizationCode.getToken().getTokenValue());
			authorizationCodeEntity.setAuthorizationCodeIssuedAt(authorizationCode.getToken().getIssuedAt());
			authorizationCodeEntity.setAuthorizationCodeExpiresAt(authorizationCode.getToken().getExpiresAt());

			entity.setAuthorizationCodeEntity(authorizationCodeEntity);
		}

		OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
				authorization.getToken(OAuth2AccessToken.class);

		if(accessToken!=null){
			var accessTokenEntity = new AccessTokenEntity();

			accessTokenEntity.setAccessTokenValue(accessToken.getToken().getTokenValue());
			accessTokenEntity.setAccessTokenIssuedAt(accessToken.getToken().getIssuedAt());
			accessTokenEntity.setAccessTokenExpiresAt(accessToken.getToken().getExpiresAt());

			if (accessToken.getToken().getScopes() != null) {
				accessTokenEntity.setScopes(accessToken.getToken().getScopes());
			}

			entity.setAccessTokenEntity(accessTokenEntity);

		}

		OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
				authorization.getToken(OAuth2RefreshToken.class);

		if(refreshToken!=null){
			var refreshTokenEntity = new RefreshTokenEntity();

			refreshTokenEntity.setRefreshTokenValue(refreshToken.getToken().getTokenValue());
			refreshTokenEntity.setRefreshTokenIssuedAt(refreshToken.getToken().getIssuedAt());
			refreshTokenEntity.setRefreshTokenExpiresAt(refreshToken.getToken().getExpiresAt());

			entity.setRefreshTokenEntity(refreshTokenEntity);
		}

		OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
				authorization.getToken(OidcIdToken.class);

		if(oidcIdToken != null){
			var oidcIdTokenEntity = new OidcIdTokenEntity();

			oidcIdTokenEntity.setOidcIdTokenValue(oidcIdToken.getToken().getTokenValue());
			oidcIdTokenEntity.setOidcIdTokenIssuedAt(oidcIdToken.getToken().getIssuedAt());
			oidcIdTokenEntity.setOidcIdTokenExpiresAt(oidcIdToken.getToken().getExpiresAt());
			//oidcIdTokenEntity.setOidcIdTokenClaims(oidcIdToken.getClaims());
			entity.setOidcIdTokenEntity(oidcIdTokenEntity);
		}

		OAuth2Authorization.Token<OAuth2UserCode> userCode =
				authorization.getToken(OAuth2UserCode.class);

		if(userCode!=null){

			var userCodeEntity = new UserCodeEntity();
			userCodeEntity.setUserCodeValue(userCode.getToken().getTokenValue());
			userCodeEntity.setUserCodeIssuedAt(userCode.getToken().getIssuedAt());
			userCodeEntity.setUserCodeExpiresAt(userCode.getToken().getExpiresAt());
			entity.setUserCode(userCodeEntity);

		}

		OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode =
				authorization.getToken(OAuth2DeviceCode.class);

		if(deviceCode!=null){
			var deviceCodeEntity = new DeviceCodeEntity();
			deviceCodeEntity.setDeviceCodeValue(deviceCode.getToken().getTokenValue());
			deviceCodeEntity.setDeviceCodeIssuedAt(deviceCode.getToken().getIssuedAt());
			deviceCodeEntity.setDeviceCodeExpiresAt(deviceCode.getToken().getExpiresAt());

			entity.setDeviceCodeEntity(deviceCodeEntity);
		}

		return entity;
	}

	private AuthorizationRequestEntity toEntity(OAuth2AuthorizationRequest object){

		if(object!=null){
			AuthorizationRequestEntity entity = new AuthorizationRequestEntity();
			entity.setAuthorizationUri(object.getAuthorizationUri());
			entity.setAuthorizationGrantType(resolveAuthorizationGrantTypeEntity(object.getGrantType().getValue()));
			entity.setClientId(object.getClientId());
			entity.setResponseType(object.getResponseType().getValue());
			entity.setRedirectUri(object.getRedirectUri());
			entity.setScopes(object.getScopes());
			entity.setState(object.getState());
			entity.setAuthorizationRequestUri(object.getAuthorizationRequestUri());

			return entity;
		}else{
			return null;
		}
	}


	private OAuth2AuthorizationRequest toObject(AuthorizationRequestEntity entity){

		OAuth2AuthorizationRequest.Builder builder =   OAuth2AuthorizationRequest.authorizationCode();

		return builder.authorizationRequestUri(entity.getAuthorizationRequestUri())
			.authorizationUri(entity.getAuthorizationUri())
			.clientId(entity.getClientId())
			.redirectUri(entity.getRedirectUri())
			.scopes(entity.getScopes())
			.state(entity.getState())
		.build();
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
		return new AuthorizationGrantType(authorizationGrantType);
	}

	public ClientEntity resolveClientEntity(String clientId){
		return clientEntityRepository.findById(UUID.fromString(clientId)).orElseThrow();
	}

	public AuthorizationGrantTypeEntity resolveAuthorizationGrantTypeEntity(String value){
		return authorizationGrantTypeEntityRepository.findByAuthorizationGrantType(value).orElseThrow();
	}
}
