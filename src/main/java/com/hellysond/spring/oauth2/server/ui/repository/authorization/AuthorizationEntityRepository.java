package com.hellysond.spring.oauth2.server.ui.repository.authorization;

import com.hellysond.spring.oauth2.server.ui.model.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizationEntityRepository extends JpaRepository<AuthorizationEntity, String> {

	Optional<AuthorizationEntity> findByState(String state);

	Optional<AuthorizationEntity> findByAuthorizationCodeEntity_AuthorizationCodeValue(String authorizationCode);

	Optional<AuthorizationEntity> findByAccessTokenEntity_AccessTokenValue(String accessToken);

	Optional<AuthorizationEntity> findByRefreshTokenEntity_RefreshTokenValue(String refreshToken);

	Optional<AuthorizationEntity> findByOidcIdTokenEntity_OidcIdTokenValue(String idToken);

	Optional<AuthorizationEntity> findByUserCode_UserCodeValue(String userCode);

	Optional<AuthorizationEntity> findByDeviceCodeEntity_DeviceCodeValue(String deviceCode);

	@Query("select a from AuthorizationEntity a where a.state = :token" +
			" or a.authorizationCodeEntity.authorizationCodeValue = :token" +
			" or a.accessTokenEntity.accessTokenValue = :token" +
			" or a.refreshTokenEntity.refreshTokenValue = :token" +
			" or a.oidcIdTokenEntity.oidcIdTokenValue = :token" +
			" or a.userCode.userCodeValue = :token" +
			" or a.deviceCodeEntity.deviceCodeValue = :token"
	)
	Optional<AuthorizationEntity> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(@Param("token") String token);
}
