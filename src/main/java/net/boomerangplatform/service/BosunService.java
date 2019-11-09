package net.boomerangplatform.service;

import java.util.List;
import java.util.Map;

import net.boomerangplatform.entity.PolicyActivityEntity;
import net.boomerangplatform.model.Policy;
import net.boomerangplatform.model.PolicyTemplate;
import net.boomerangplatform.model.PolicyInsights;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.PolicyValidation;
import net.boomerangplatform.model.PolicyViolations;

public interface BosunService {

  List<PolicyTemplate> getAllTemplates();

  Map<String, String> getAllOperators();

  List<Policy> getPoliciesByTeamId(String ciTeamId);

  Policy addPolicy(Policy policy);

  Policy updatePolicy(Policy policy);

  Policy getPolicyById(String ciPolicyId);

  List<PolicyInsights> getInsights(String ciTeamId);

  List<PolicyViolations> getViolations(String ciTeamId);

  PolicyResponse deletePolicy(String ciPolicyId);

  PolicyActivityEntity validatePolicy(PolicyValidation policyValidation);

PolicyTemplate getTemplate(String templateId);

PolicyTemplate addTemplate(PolicyTemplate template);

PolicyTemplate updateTemplate(String templateId,PolicyTemplate template);
}
