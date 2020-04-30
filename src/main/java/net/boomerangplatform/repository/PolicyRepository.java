package net.boomerangplatform.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.boomerangplatform.entity.PolicyEntity;
import net.boomerangplatform.model.Scope;

public interface PolicyRepository extends MongoRepository<PolicyEntity, String> {

  List<PolicyEntity> findByTeamId(String teamId);

  List<PolicyEntity> findByScope(Scope global);

}
