package net.boomerangplatform.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.boomerangplatform.entity.PolicyTeamEntity;

@Repository
public interface PolicyTeamRepository extends MongoRepository<PolicyTeamEntity, String> {

	Optional<PolicyTeamEntity> findByName(String name);
}
