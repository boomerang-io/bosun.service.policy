package net.boomerangplatform.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.PolicyDefinitionEntity;

public interface PolicyDefinitionRepository
    extends MongoRepository<PolicyDefinitionEntity, String> {

}
