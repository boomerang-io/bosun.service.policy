package net.boomerangplatform.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.CiPolicyDefinitionEntity;

public interface CiPolicyDefinitionRepository
    extends MongoRepository<CiPolicyDefinitionEntity, String> {

}
