package net.boomerangplatform.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;
import net.boomerangplatform.mongo.entity.CiPolicyDefinitionEntity;
import net.boomerangplatform.mongo.entity.CiPolicyEntity;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.mongo.service.CiPolicyService;

@Service
public class CitadelServiceImpl implements CitadelService {

  @Autowired
  private CiPolicyService ciPolicyService;

  @Override
  public List<CiPolicyDefinition> getAllDefinitions() {
    List<CiPolicyDefinitionEntity> entitis = ciPolicyService.findAllDefinitions();
    List<CiPolicyDefinition> descriptions = new ArrayList<>();

    entitis.forEach(entity -> {
      CiPolicyDefinition description = new CiPolicyDefinition();
      BeanUtils.copyProperties(entity, description);
      descriptions.add(description);
    });

    return descriptions;
  }

  @Override
  public Map<String, String> getAllOperators() {
    Map<String, String> operators = new LinkedHashMap<>();
    for (OperatorType type : OperatorType.values()) {
      operators.put(type.name(), type.getOperator());
    }

    return operators;
  }

  @Override
  public List<CiPolicy> getPoliciesByTeamId(String teamId) {
    List<CiPolicyEntity> entities = ciPolicyService.findByTeamId(teamId);
    List<CiPolicy> policies = new ArrayList<>();

    entities.forEach(entity -> {
      CiPolicy policy = new CiPolicy();
      BeanUtils.copyProperties(entity, policy);
      policies.add(policy);
    });

    return policies;
  }

  @Override
  public CiPolicy addPolicy(String teamId, CiPolicy policy) {
    policy.setTeamId(teamId);
    CiPolicyEntity entity = new CiPolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity = ciPolicyService.add(entity);
    policy.setId(entity.getId());

    return policy;

  }

  @Override
  public CiPolicy updatePolicy(String teamId, CiPolicy policy) {
    policy.setTeamId(teamId);
    CiPolicyEntity entity = ciPolicyService.findById(policy.getId());
    BeanUtils.copyProperties(policy, entity);
    ciPolicyService.update(entity);

    return policy;
  }

}
