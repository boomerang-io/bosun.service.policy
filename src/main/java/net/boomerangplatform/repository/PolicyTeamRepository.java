package net.boomerangplatform.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.boomerangplatform.entity.PolicyTeamEntity;

public interface PolicyTeamRepository extends MongoRepository<PolicyTeamEntity, String> {

	Optional<PolicyTeamEntity> findByName(String name);
}
