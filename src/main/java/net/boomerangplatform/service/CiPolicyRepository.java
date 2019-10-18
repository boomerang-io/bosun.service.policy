package net.boomerangplatform.service;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.CiPolicyEntity;
import net.boomerangplatform.mongo.model.Scope;

public interface CiPolicyRepository extends MongoRepository<CiPolicyEntity, String> {

  List<CiPolicyEntity> findByTeamId(String teamId);

  List<CiPolicyEntity> findByScope(Scope global);

}
