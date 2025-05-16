package net.boomerangplatform.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.boomerangplatform.entity.PolicyActivityEntity;

@Repository
public interface PolicyActivityCustom {
	
	List<PolicyActivityEntity> findTopDistinctViolationsByPolicyIdAndReferenceId(String policyId);
}
