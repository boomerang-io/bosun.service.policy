package net.boomerangplatform.service;

import java.util.List;

import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityCustom {
	
	List<PolicyActivityEntity> findTopDistinctViolationsByPolicyIdAndReferenceId(String policyId);
}
