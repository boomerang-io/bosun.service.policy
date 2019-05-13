package net.boomerangplatform.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyConfig;
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

    List<CiPolicyDefinitionEntity> definitions = ciPolicyService.findAllDefinitions();

    entities.forEach(entity -> {
      CiPolicy policy = new CiPolicy();
      BeanUtils.copyProperties(entity, policy, "definitions");
      
      entity.getDefinitions().forEach(definition -> {
        CiPolicyConfig config = new CiPolicyConfig();
        BeanUtils.copyProperties(definition, config);
        config.setCiPolicyDefinition(getDefinition(definitions, definition.getCiPolicyDefinitionId()));
        policy.addDefinition(config);
      });

      policies.add(policy);
    });

    return policies;
  }

  @Override
  public CiPolicy addPolicy(CiPolicy policy) {
    CiPolicyEntity entity = new CiPolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity = ciPolicyService.add(entity);
    policy.setId(entity.getId());

    return policy;
  }

  @Override
  public CiPolicy updatePolicy(CiPolicy policy) {
    CiPolicyEntity entity = ciPolicyService.findById(policy.getId());
    BeanUtils.copyProperties(policy, entity);
    ciPolicyService.update(entity);

    return policy;
  }

  private static CiPolicyDefinition getDefinition(List<CiPolicyDefinitionEntity> definitions, String definitionId) {
    CiPolicyDefinition policyDefinition = new CiPolicyDefinition();
    CiPolicyDefinitionEntity definitionEntity = definitions.stream()
        .filter(d -> definitionId.equals(d.getId())).findFirst().orElse(null);
    Assert.notNull(definitionEntity, "The definition is missing!");
    BeanUtils.copyProperties(definitionEntity, policyDefinition);
    
    return policyDefinition;
  }
}
