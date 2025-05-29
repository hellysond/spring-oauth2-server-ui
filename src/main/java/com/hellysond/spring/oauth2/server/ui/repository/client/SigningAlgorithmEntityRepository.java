package com.hellysond.spring.oauth2.server.ui.repository.client;

import com.hellysond.spring.oauth2.server.ui.model.entity.SigningAlgorithmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SigningAlgorithmEntityRepository extends JpaRepository<SigningAlgorithmEntity, UUID> {

    Optional<SigningAlgorithmEntity> findBySigningAlgorithm(String value);

}
