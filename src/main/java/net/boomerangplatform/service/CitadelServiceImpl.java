package net.boomerangplatform.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.model.CiPoliciesActivities;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.Result;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiPolicyDefinitionEntity;
import net.boomerangplatform.mongo.entity.CiPolicyEntity;
import net.boomerangplatform.mongo.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
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
  private CiComponentVersionService ciComponentVersionService;
	
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
  
  Logger logger = LogManager.getLogger();

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
  public CiPolicy getPolicyById(String ciPolicyId) {
    CiPolicyEntity entity = ciPolicyService.findById(ciPolicyId);

    CiPolicy policy = new CiPolicy();
    BeanUtils.copyProperties(entity, policy);

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
	  
	  CiComponentVersionEntity ciComponentVersionEntity = ciComponentVersionService.findVersionWithId(ciVersionId);
	  
	  if (ciComponentVersionEntity == null) {
		  return new CiPoliciesActivities();
	  }
	  
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
			  SonarQubeReport sonarQubeReport = repositoryService.getSonarQubeReport(ciComponentId, ciComponentVersionEntity.getName());
			  
			  if (sonarQubeReport.getMeasures() != null) {
				  logger.info(sonarQubeReport.getMeasures().getComplexity() + ", " + sonarQubeReport.getMeasures().getNcloc() + ", " + sonarQubeReport.getMeasures().getViolations());  
			  }		
			  
			  ObjectMapper mapper = new ObjectMapper(); 
			  JsonNode data = mapper.convertValue(sonarQubeReport, JsonNode.class);			 
			  
			  try {
				  logger.info(mapper.writeValueAsString(data));	  
			  }
			  catch (JsonProcessingException e) {
				  logger.info(e);  
			  }			  
			  
			  DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(), policyDefinitionEntity.getKey(), data);
			  
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
			  DependencyGraph dependencyGraph = repositoryService.getDependencyGraph(ciComponentId, ciComponentVersionEntity.getName());
			  
			  logger.info(dependencyGraph.getComponents().size());
			  
			  ObjectMapper mapper = new ObjectMapper(); 
			  JsonNode data = mapper.convertValue(dependencyGraph, JsonNode.class);
			  
			  try {
				  logger.info(mapper.writeValueAsString(data));	  
			  }
			  catch (JsonProcessingException e) {
				logger.info(e);  
			  }	
			  
			  DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(), policyDefinitionEntity.getKey(), data);
			  
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
  
  private DataResponse callOpenPolicyAgentClient(String policyDefinitionId, String policyDefinitionKey, JsonNode data) {
	  
	  DataRequestPolicy dataRequestPolicy = new DataRequestPolicy();
	  dataRequestPolicy.setId(policyDefinitionId);
	  dataRequestPolicy.setKey(policyDefinitionKey);
	  
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
