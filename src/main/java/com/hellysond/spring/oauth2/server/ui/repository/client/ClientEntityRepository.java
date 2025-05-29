package com.hellysond.spring.oauth2.server.ui.repository.client;

import com.hellysond.spring.oauth2.server.ui.model.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientEntityRepository extends JpaRepository<ClientEntity, UUID> {
	Optional<ClientEntity> findByClientId(String clientId);
}
