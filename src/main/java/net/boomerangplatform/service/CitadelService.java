package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;

import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.mongo.entity.CiPolicyActivityEntity;

public interface CitadelService {

  List<CiPolicyDefinition> getAllDefinitions();

  Map<String, String> getAllOperators();

  List<CiPolicy> getPoliciesByTeamId(String teamId);

  CiPolicy addPolicy(CiPolicy policy);

  CiPolicy updatePolicy(CiPolicy policy);

  CiPolicy getPolicyById(String ciPolicyId);
  
  CiPolicyActivityEntity validatePolicy(String ciComponentId, String ciVersionId, String ciPolicyId);
  
  Map<String, Integer> getInsights(String teamId);
}
