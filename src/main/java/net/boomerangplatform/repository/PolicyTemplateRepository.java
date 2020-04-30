package net.boomerangplatform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.PolicyTemplateEntity;

public interface PolicyTemplateRepository
    extends MongoRepository<PolicyTemplateEntity, String> {

}
