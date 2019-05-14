package net.boomerangplatform.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.citadel.model.CiPoliciesActivities;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;
import net.boomerangplatform.citadel.model.Result;
import net.boomerangplatform.mongo.entity.CiPolicyDefinitionEntity;
import net.boomerangplatform.mongo.entity.CiPolicyEntity;
import net.boomerangplatform.mongo.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.mongo.service.CiPolicyActivityService;
import net.boomerangplatform.mongo.service.CiPolicyDefinitionService;
import net.boomerangplatform.mongo.service.CiPolicyService;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataRequestInput;
import net.boomerangplatform.opa.model.DataRequestPolicy;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;
import net.boomerangplatform.repository.service.RepositoryService;

@Service
public class CitadelServiceImpl implements CitadelService {

  @Autowired
  private CiPolicyService ciPolicyService;
  
  @Autowired
  private CiPolicyDefinitionService ciPolicyDefinitionService;
  
  @Autowired
  private CiPolicyActivityService ciPolicyActivityService;
  
  @Autowired
  private RepositoryService repositoryService;
  
  @Autowired
  private OpenPolicyAgentClient openPolicyAgentClient;

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
//        config.setCiPolicyDefinition(getDefinition(definitions, definition.getCiPolicyDefinitionId()));
//        
//        policy.addDefinition(config);
      });
      policies.add(policy);
    });

    return policies;
  }

  @Override
  public CiPolicy getPolicyById(String ciPolicyId) {
    CiPolicyEntity entity = ciPolicyService.findById(ciPolicyId);

    List<CiPolicyDefinitionEntity> definitions = ciPolicyService.findAllDefinitions();

    CiPolicy policy = new CiPolicy();
    BeanUtils.copyProperties(entity, policy, "definitions");

    entity
        .getDefinitions()
        .forEach(
            definition -> {
              CiPolicyConfig config = new CiPolicyConfig();
              BeanUtils.copyProperties(definition, config);
              config.setCiPolicyDefinition(
                  getDefinition(definitions, definition.getCiPolicyDefinitionId()));

              policy.addDefinition(config);
            });

    return policy;
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
  
  @Override
  public CiPoliciesActivities validatePolicy(String ciComponentId, String ciVersionId, String ciPolicyId) {
	  
	  CiPoliciesActivities policiesActivities = new CiPoliciesActivities();
	  policiesActivities.setCiComponentId(ciComponentId);
	  policiesActivities.setCiPolicyId(ciPolicyId);
	  policiesActivities.setCiVersionId(ciVersionId);
	  
	  List<Result> results = new ArrayList<Result>();
	  
	  boolean overallResult = true;
	  
	  CiPolicyEntity policyEntity = ciPolicyService.findById(ciPolicyId);
	  for (CiPolicyConfig policyConfig : policyEntity.getDefinitions()) {
		  
		  CiPolicyDefinitionEntity policyDefinitionEntity = ciPolicyDefinitionService.findById(policyConfig.getCiPolicyDefinitionId());
		  
		  if (policyDefinitionEntity.getKey().equalsIgnoreCase("static_code_analysis")) {
			  SonarQubeReport sonarQubeReport = repositoryService.getSonarQubeReport(ciComponentId, ciVersionId);
			  
			  DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(), policyDefinitionEntity.getKey(), sonarQubeReport);
			  
			  Result result = new Result();
			  result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
			  result.setDetail(dataResponse.getResult().getDetail().asText());
			  result.setValid(dataResponse.getResult().getValid());
			  
			  if (!dataResponse.getResult().getValid()) {
				  overallResult = false;
			  }
			  
			  results.add(result);
		  }
		  else if (policyDefinitionEntity.getKey().equalsIgnoreCase("whitelist")) {
			  DependencyGraph dependencyGraph = repositoryService.getDependencyGraph(ciComponentId, ciVersionId);
			  
			  DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(), policyDefinitionEntity.getKey(), dependencyGraph);
			  
			  Result result = new Result();
			  result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
			  result.setDetail(dataResponse.getResult().getDetail().asText());
			  result.setValid(dataResponse.getResult().getValid());
			  
			  if (!dataResponse.getResult().getValid()) {
				  overallResult = false;
			  }
			  
			  results.add(result);			  			 		  
		  }
	  }
	  
	  policiesActivities.setValid(overallResult);
	  policiesActivities.setResults(results);
	  
//	  policiesActivities = ciPolicyActivityService.save(policiesActivities);
	  
	  return policiesActivities;
  }
  
  private DataResponse callOpenPolicyAgentClient(String policyDefinitionId, String policyDefinitionKey, Object request) {
	  
	  DataRequestPolicy dataRequestPolicy = new DataRequestPolicy();
	  dataRequestPolicy.setId(policyDefinitionId);
	  dataRequestPolicy.setKey(policyDefinitionKey);			  			 
	  
	  ObjectMapper mapper = new ObjectMapper(); 
	  JsonNode data = mapper.convertValue(request, JsonNode.class);		  			 
	  
	  DataRequestInput dataRequestInput = new DataRequestInput();
	  dataRequestInput.setPolicy(dataRequestPolicy);
	  dataRequestInput.setData(data);
	  
	  DataRequest dataRequest = new DataRequest();
	  dataRequest.setInput(dataRequestInput);
	 
	  return openPolicyAgentClient.validateData(dataRequest);
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
