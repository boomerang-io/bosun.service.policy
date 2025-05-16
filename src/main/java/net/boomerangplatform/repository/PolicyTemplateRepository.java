package net.boomerangplatform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.boomerangplatform.entity.PolicyTemplateEntity;

@Repository
public interface PolicyTemplateRepository
    extends MongoRepository<PolicyTemplateEntity, String> {

}
