package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.entity.CiPolicyActivityEntity;

public interface BosunService {

  List<CiPolicyDefinition> getAllDefinitions();

  Map<String, String> getAllOperators();

  List<CiPolicy> getPoliciesByTeamId(String ciTeamId);

  CiPolicy addPolicy(CiPolicy policy);

  CiPolicy updatePolicy(CiPolicy policy);

  CiPolicy getPolicyById(String ciPolicyId);
  
  List<CiPolicyInsights> getInsights(String ciTeamId);
  
  List<CiPolicyViolations> getViolations(String ciTeamId);

  PolicyResponse deletePolicy(String ciPolicyId);

CiPolicyActivityEntity validatePolicy(String ciPolicyId,String ciComponentActivityId,String ciComponentId,String ciComponentVersion);
}