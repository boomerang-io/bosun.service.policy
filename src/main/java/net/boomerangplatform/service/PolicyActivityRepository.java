package net.boomerangplatform.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityRepository
    extends MongoRepository<PolicyActivityEntity, String>, PolicyActivityCustom {

  //	@Aggregation("{ $sort: { createdDate: -1 } }, { $match: { policyId: $?0 } }, { $group: { _id: {
  // referenceId: $referenceId }, documents : { $push: $$ROOT } } }, { $replaceRoot: { newRoot: {
  // $arrayElemAt: [$documents, 0] } } }, { $match: { valid: false } }")
  //	List<PolicyActivityEntity> findTopDistinctViolationsByPolicyIdAndReferenceId(String policyId);
}
