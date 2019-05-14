package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;

public interface CitadelService {

  List<CiPolicyDefinition> getAllDefinitions();

  Map<String, String> getAllOperators();

  List<CiPolicy> getPoliciesByTeamId(String teamId);

  CiPolicy addPolicy(CiPolicy policy);

  CiPolicy updatePolicy(CiPolicy policy);

  CiPolicy getPolicyById(String ciPolicyId);
}
