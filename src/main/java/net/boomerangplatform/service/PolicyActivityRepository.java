package net.boomerangplatform.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityRepository extends MongoRepository<PolicyActivityEntity, String>, PolicyActivityCustom {

//	List<PolicyActivityEntity> findByCiTeamIdAndValidAndCreatedDateAfter(String ciTeamId, Boolean valid, Date date);
//	List<PolicyActivityEntity> findByCiComponentActivityIdAndValid(String ciComponentActivityId, Boolean valid);
//	List<PolicyActivityEntity> findTopByReferenceIdAndPolicyIdOrderByCreationDateDesc(String referenceId, String policyId);
}
