package com.hellysond.spring.oauth2.server.ui.repository.client;

import com.hellysond.spring.oauth2.server.ui.model.entity.AuthenticationMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthenticationMethodEntityRepository extends JpaRepository<AuthenticationMethodEntity,UUID> {

    Optional<AuthenticationMethodEntity> findByAuthenticationMethod(String value);

}
