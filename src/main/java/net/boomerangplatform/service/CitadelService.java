package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;

import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.mongo.entity.CiPolicyActivityEntity;

public interface CitadelService {

  List<CiPolicyDefinition> getAllDefinitions();

  Map<String, String> getAllOperators();

  List<CiPolicy> getPoliciesByTeamId(String teamId);

  CiPolicy addPolicy(CiPolicy policy);

  CiPolicy updatePolicy(CiPolicy policy);

  CiPolicy getPolicyById(String ciPolicyId);
  
  CiPolicyActivityEntity validatePolicy(String ciComponentActivityId, String ciPolicyId);
  
  List<CiPolicyInsights> getInsights(String teamId);
  
  List<CiPolicyViolations> getViolations(String ciTeamId);
}
