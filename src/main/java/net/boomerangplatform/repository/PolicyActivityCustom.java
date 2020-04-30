package net.boomerangplatform.repository;

import java.util.List;

import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityCustom {
	
	List<PolicyActivityEntity> findTopDistinctViolationsByPolicyIdAndReferenceId(String policyId);
}
