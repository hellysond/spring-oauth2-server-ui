package com.hellysond.spring.oauth2.server.ui.repository.authorization;

import com.hellysond.spring.oauth2.server.ui.model.entity.AuthorizationGrantTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorizationGrantTypeEntityRepository extends JpaRepository<AuthorizationGrantTypeEntity, UUID> {

     Optional<AuthorizationGrantTypeEntity> findByAuthorizationGrantType(String value);
}
