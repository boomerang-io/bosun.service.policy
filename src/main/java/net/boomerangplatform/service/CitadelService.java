package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;

import net.boomerangplatform.model.CiPoliciesActivities;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;

public interface CitadelService {

  List<CiPolicyDefinition> getAllDefinitions();

  Map<String, String> getAllOperators();

  List<CiPolicy> getPoliciesByTeamId(String teamId);

  CiPolicy addPolicy(CiPolicy policy);

  CiPolicy updatePolicy(CiPolicy policy);

  CiPolicy getPolicyById(String ciPolicyId);
  
  CiPoliciesActivities validatePolicy(String ciComponentId, String ciVersionId, String ciPolicyId);
}
