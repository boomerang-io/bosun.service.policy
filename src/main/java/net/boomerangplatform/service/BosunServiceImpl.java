package net.boomerangplatform.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.entity.PolicyActivityEntity;
import net.boomerangplatform.entity.PolicyDefinitionEntity;
import net.boomerangplatform.entity.PolicyEntity;
import net.boomerangplatform.model.Policy;
import net.boomerangplatform.model.PolicyActivitiesInsights;
import net.boomerangplatform.model.PolicyConfig;
import net.boomerangplatform.model.PolicyDefinition;
import net.boomerangplatform.model.PolicyInsights;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.PolicyValidation;
import net.boomerangplatform.model.PolicyViolation;
import net.boomerangplatform.model.PolicyViolations;
import net.boomerangplatform.model.Results;
import net.boomerangplatform.model.ResultsViolation;
import net.boomerangplatform.model.Scope;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataRequestInput;
import net.boomerangplatform.opa.model.DataRequestPolicy;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.model.DataResponseResultViolation;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;
import net.boomerangplatform.repository.PolicyActivityRepository;
import net.boomerangplatform.repository.PolicyDefinitionRepository;
import net.boomerangplatform.repository.PolicyRepository;
import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;
import net.boomerangplatform.repository.service.RepositoryService;

@Service
public class BosunServiceImpl implements BosunService {

  @Value("${insights.period.months}")
  private String insightsPeriodMonths;

  @Autowired
  private PolicyRepository policyRepository;

  @Autowired
  private PolicyDefinitionRepository policyDefinitionRepository;

  @Autowired
  private PolicyActivityRepository policyActivityRepository;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private OpenPolicyAgentClient openPolicyAgentClient;

  @Autowired
  private Clock clock;

  private static final Logger LOGGER = LogManager.getLogger();

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Override
  public List<PolicyDefinition> getAllDefinitions() {
    List<PolicyDefinitionEntity> entities = policyDefinitionRepository.findAll(new Sort(Sort.Direction.ASC, "order"));
    List<PolicyDefinition> descriptions = new ArrayList<>();

    entities.forEach(entity -> {
      PolicyDefinition description = new PolicyDefinition();
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
  public List<Policy> getPoliciesByTeamId(String teamId) {
    List<PolicyEntity> entities = policyRepository.findByTeamId(teamId);
    List<Policy> policies = new ArrayList<>();

    /** TODO: Fix compilation error here. 
    entities.forEach(entity -> {
      Policy policy = new Policy();
      BeanUtils.copyProperties(entity, policy);
      policy.setStages(getStagesForPolicy(teamId, entity.getId()));
      policies.add(policy);
    });

    List<PolicyEntity> globalPolicies = policyRepository.findByScope(Scope.global);
    globalPolicies.forEach(entity -> {
      Policy policy = new Policy();
      BeanUtils.copyProperties(entity, policy);
      policy.setStages(getStagesForGlobalPolicy(entity.getId()));
      policies.add(policy);
    }); */

    return policies;
  }

  @Override
  public Policy getPolicyById(String ciPolicyId) {
    PolicyEntity entity = policyRepository.findById(ciPolicyId).orElse(null);

    Policy policy = new Policy();
    BeanUtils.copyProperties(entity, policy);

    return policy;
  }

  @Override
  public Policy addPolicy(Policy policy) {
    policy.setCreatedDate(fromLocalDate(LocalDate.now(clock())));

    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    policy.setScope(Scope.team);
    PolicyEntity entity = new PolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity = policyRepository.insert(entity);
    policy.setId(entity.getId());

    return policy;
  }


  @Override
  public Policy updatePolicy(Policy policy) {
    PolicyEntity entity = policyRepository.findById(policy.getId()).orElse(null);
    policy.setScope(Scope.team);
    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    BeanUtils.copyProperties(policy, entity);
    policyRepository.save(entity);

    return policy;
  }

	@Override
	public PolicyActivityEntity validatePolicy(PolicyValidation policyValidation) {

		PolicyEntity policyEntity = policyRepository.findById(policyValidation.getPolicyId()).orElse(null);

		final PolicyActivityEntity policiesActivities = new PolicyActivityEntity();
		policiesActivities.setTeamId(policyEntity.getTeamId());
		policiesActivities.setPolicyId(policyEntity.getId());
		policiesActivities.setLabels(policyValidation.getLabels());
		policiesActivities.setReferenceId(policyValidation.getReferenceId());
		policiesActivities.setCreatedDate(fromLocalDate(LocalDate.now(clock)));
		policiesActivities.setValid(true);

		List<Results> results = new ArrayList<>();

		if (policyEntity != null && policyEntity.getDefinitions() != null) {
			policyEntity.getDefinitions().stream()
					.filter(policyConfig -> !CollectionUtils.isEmpty(policyConfig.getRules())).forEach(policyConfig -> {

						PolicyDefinitionEntity policyDefinitionEntity = policyDefinitionRepository
								.findById(policyConfig.getPolicyDefinitionId()).orElse(null);

						Results result = getResult(policyValidation.getLabels(),
								policyConfig, policyDefinitionEntity);

						if (result != null) {
							if (!result.getValid()) {
								policiesActivities.setValid(false);
								if (result.getViolations().isEmpty()) {
									ResultsViolation resultsViolation = new ResultsViolation();
									resultsViolation.setMetric(policyDefinitionEntity.getName());
									resultsViolation.setMessage("No data exists for component/version");
									resultsViolation.setValid(false);
									result.getViolations().add(resultsViolation);
								}
							}
							results.add(result);
						}
					});
		}

		policiesActivities.setResults(results);
		return policyActivityRepository.save(policiesActivities);

	}

  @Override
  public List<PolicyInsights> getInsights(String ciTeamId) {
    Map<String, PolicyInsights> insights = new HashMap<>();
//    LocalDate date = LocalDate.now(clock).minusMonths(Integer.valueOf(insightsPeriodMonths));
//
//    List<PolicyActivityEntity> activities = policyActivityRepository
//        .findByCiTeamIdAndValidAndCreatedDateAfter(ciTeamId, false, fromLocalDate(date));
//
//    for (PolicyActivityEntity activity : activities) {
//      String ciPolicyId = activity.getPolicyId();
//
//      PolicyInsights policyInsights = insights.get(ciPolicyId);
//      if (policyInsights == null && policyRepository.findById(ciPolicyId).isPresent()) {
//        Policy ciPolicy = getPolicyById(ciPolicyId);
//        policyInsights = new PolicyInsights();
//        policyInsights.setCiPolicyId(ciPolicy.getId());
//        policyInsights.setCiPolicyName(ciPolicy.getName());
//        policyInsights.setCiPolicyCreatedDate(ciPolicy.getCreatedDate());
//      }
//
//      PolicyActivitiesInsights policyActivitiesInsights =
//          getPolicyActivitiesInsights(activity, policyInsights);
//
//      if(policyInsights != null) {
//      policyInsights.addInsights(policyActivitiesInsights);
//      insights.put(ciPolicyId, policyInsights);
//      }
//     
//    }

    return new ArrayList<>(insights.values());
  }

  private List<PolicyConfig> getFilteredDefinition(List<PolicyConfig> policyDefinitions) {
    List<PolicyConfig> filteredDefinitions = new ArrayList<>();
    for (PolicyConfig definition : policyDefinitions) {
      if (!definition.getRules().isEmpty()) {
        filteredDefinitions.add(definition);
      }
    }
    return filteredDefinitions;
  }

  private PolicyActivitiesInsights getPolicyActivitiesInsights(PolicyActivityEntity activity,
      PolicyInsights policyInsights) {
    Integer failCount = getFaildedCount(activity);

    PolicyActivitiesInsights policyActivitiesInsights = null;

//    if(policyInsights != null) {
//    for (PolicyActivitiesInsights activites : policyInsights.getInsights()) {
//      if (activites.getPolicyActivityId().equalsIgnoreCase(activity.getCiComponentActivityId())) {
//        policyActivitiesInsights = activites;
//        policyInsights.removeInsights(activites);
//        break;
//      }
//    }
//    }
//
//    if (policyActivitiesInsights == null) {
//    	policyActivitiesInsights = new PolicyActivitiesInsights();
//    	policyActivitiesInsights.setPolicyActivityId(activity.getCiComponentActivityId());
//    	policyActivitiesInsights.setPolicyActivityCreatedDate(activity.getCreatedDate());
//    	policyActivitiesInsights.setViolations(failCount);
//    } else {
//      policyActivitiesInsights
//          .setViolations(policyActivitiesInsights.getViolations() + failCount);
//    }
    return policyActivitiesInsights;
  }

	@Override
	public List<PolicyViolations> getViolations(String teamId) {

		Map<String, PolicyViolations> violationsMap = new HashMap<>();
		
		LOGGER.info("team.id=" + teamId);

		List<PolicyEntity> policyEntities = policyRepository.findByTeamId(teamId);

		for (PolicyEntity policyEntity : policyEntities) {

			LOGGER.info("policy.name=" + policyEntity.getName());
			
			List<PolicyActivityEntity> policyActivityEntities = policyActivityRepository.findTopDistinctViolationsByPolicyIdAndReferenceId(policyEntity.getId());

			LOGGER.info("policyActivities.size=" + policyActivityEntities.size());

			setViolations(violationsMap, policyActivityEntities);
		}

		return new ArrayList<>(violationsMap.values());
	}

	private void setViolations(Map<String, PolicyViolations> violationsMap, List<PolicyActivityEntity> policyActivities) {
		for (PolicyActivityEntity policyActivity : policyActivities) {

			LOGGER.info(new JSONObject(policyActivity).toString());
			PolicyEntity policy = policyRepository.findById(policyActivity.getPolicyId()).orElse(null);
			
			if (policy == null) {
				continue;
			}

			LOGGER.info("policy.name=" + policy.getName());

			StringBuilder key = new StringBuilder();
			key.append(policy.getId()).append(policyActivity.getReferenceId());

			LOGGER.info("key=" + key.toString());

			PolicyViolations violation = getViolation(key.toString(), policyActivity, policy, violationsMap.get(key.toString()));

			violationsMap.put(key.toString(), violation);
		}
	}

	private PolicyViolations getViolation(String key, PolicyActivityEntity policyActivity, PolicyEntity policy, PolicyViolations violation) {

		if (violation == null) {
			violation = new PolicyViolations();
			violation.setId(key);
			violation.setPolicyId(policy.getId());
			violation.setPolicyName(policy.getName());
			violation.setReferenceId(policyActivity.getReferenceId());
			violation.setLabels(policyActivity.getLabels());
			violation.setNbrViolations(0);
			violation.setViolations(null);
			violation.setPolicyActivityCreatedDate(policyActivity.getCreatedDate());
		} else if (policyActivity.getCreatedDate().after(violation.getPolicyActivityCreatedDate())) {
			violation.setNbrViolations(0);
			violation.setViolations(null);
			violation.setPolicyActivityCreatedDate(policyActivity.getCreatedDate());
		}

		violation.setNbrViolations(violation.getNbrViolations() + getViolationsTotal(policyActivity));
		violation.getViolations().addAll(getViolationsResults(policyActivity));
		violation.getPolicyDefinitionTypes()
				.addAll(getViolationsDefinitionTypes(violation.getPolicyDefinitionTypes(), policyActivity));

		return violation;
	}
  
  private List<PolicyViolation> getViolationsResults(PolicyActivityEntity policyActivity) {
	  List<PolicyViolation> resultsViolations = new ArrayList<>();
	  for (Results results : policyActivity.getResults()) {
		  if (!results.getValid()) {
			  if (!results.getViolations().isEmpty()) {
				  resultsViolations.addAll(getPolicyViolations(results.getViolations()));	  
			  }			  	  
		  }		  
	  }	  
	  return resultsViolations;
  }
  
  private List<PolicyViolation> getPolicyViolations(List<ResultsViolation> violations) {
	  List<PolicyViolation> policyViolations = new ArrayList<>();
	  for (ResultsViolation resultsViolation : violations) {
		  PolicyViolation policyViolation = new PolicyViolation();
		  policyViolation.setMetric(resultsViolation.getMetric());
		  policyViolation.setMessage(resultsViolation.getMessage());
		  policyViolation.setValid(resultsViolation.getValid());
		  policyViolations.add(policyViolation);
	  }
	  return policyViolations;
  }

  private Integer getViolationsTotal(PolicyActivityEntity policyActivity) {
    Integer violationsTotal = 0;
    for (Results result : policyActivity.getResults()) {
      if (!result.getValid()) {
        violationsTotal++;
      }
    }
    return violationsTotal;
  }
  
	private List<String> getViolationsDefinitionTypes(List<String> current, PolicyActivityEntity policyActivity) {
		List<String> violationsDefinitionTypes = new ArrayList<>();
		for (Results result : policyActivity.getResults()) {
			if (!result.getValid()) {
				String policyDefinitionId = result.getPolicyDefinitionId() != null ? result.getPolicyDefinitionId() : "";
				LOGGER.info("policyDefinitionId=" + policyDefinitionId);
				PolicyDefinitionEntity policyDefinitionEntity = policyDefinitionRepository.findById(policyDefinitionId).orElse(null);
				if (policyDefinitionEntity != null && !current.contains(policyDefinitionEntity.getName())) {
					LOGGER.info("policyDefinitionName=" + policyDefinitionEntity.getName());
					violationsDefinitionTypes.add(policyDefinitionEntity.getName());
				}
			}
		}		
		return violationsDefinitionTypes;
	}

  private Results getResult(Map<String, String> labels, PolicyConfig policyConfig,
      PolicyDefinitionEntity policyDefinition) {

    if (policyDefinition == null) {
      return null;
    }

    Results result = getDefaultResult(policyDefinition.getId());
    String key = policyDefinition.getKey().toLowerCase(Locale.US);
    switch (key) {
      case "static_code_analysis":
        SonarQubeReport sonarQubeReport =
            repositoryService.getSonarQubeReport(labels.get("sonarqubeId"), labels.get("versionId"));
        result = getResults(policyDefinition, policyConfig, getJsonNode(sonarQubeReport, key));
        break;
      case "unit_tests":
        SonarQubeReport sonarQubeTestCoverage =
            repositoryService.getSonarQubeTestCoverage(labels.get("sonarqubeId"), labels.get("versionId"));
        result =
            getResults(policyDefinition, policyConfig, getJsonNode(sonarQubeTestCoverage, key));
        break;
      case "package_safelist":
        DependencyGraph dependencyGraph =
            repositoryService.getDependencyGraph(labels.get("artifactoryId"), labels.get("versionId"));
        result = getResults(policyDefinition, policyConfig, getJsonNode(dependencyGraph, key));
        break;
      case "cve_safelist":
      case "security_issue_analysis":
        ArtifactSummary summary = repositoryService.getArtifactSummary(labels.get("artifactoryId"), labels.get("versionId"));
        if (!summary.getArtifacts().isEmpty()) {
          result = getResults(policyDefinition, policyConfig,
              getJsonNode(summary.getArtifacts().get(0).getIssues(), key));
        }
        break;
      default:
        result = null;
        break;
    }
    return result;
  }

  private Results getDefaultResult(String policyDefinitionId) {
    Results result = new Results();
    result.setPolicyDefinitionId(policyDefinitionId);
    result.setViolations(new ArrayList<ResultsViolation>());
    result.setValid(false);

    return result;
  }

  private Results getResults(PolicyDefinitionEntity policyDefinitionEntity,
      PolicyConfig policyConfig, JsonNode data) {

    DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
        policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

    Results result = new Results();
    result.setPolicyDefinitionId(policyDefinitionEntity.getId());    
    result.setViolations(getResultsViolation(dataResponse.getResult().getViolations()));
    result.setValid(dataResponse.getResult().getValid());

    return result;
  }
  
  private List<ResultsViolation> getResultsViolation(List<DataResponseResultViolation> dataResponseResultViolations) {
	  List<ResultsViolation> resultsViolations = new ArrayList<ResultsViolation>();
	  for (DataResponseResultViolation dataResponseResultViolation : dataResponseResultViolations) {
		  ResultsViolation resultsViolation = new ResultsViolation();
		  resultsViolation.setMessage(dataResponseResultViolation.getMessage());
		  resultsViolation.setMetric(dataResponseResultViolation.getMetric());
		  resultsViolation.setValid(dataResponseResultViolation.getValid());
		  resultsViolations.add(resultsViolation);		  
	  }
	  return resultsViolations;
  }

  private DataResponse callOpenPolicyAgentClient(String policyDefinitionId,
      String policyDefinitionKey, List<Map<String, String>> rules, JsonNode data) {

    DataRequestPolicy dataRequestPolicy = new DataRequestPolicy();
    dataRequestPolicy.setId(policyDefinitionId);
    dataRequestPolicy.setKey(policyDefinitionKey);
    dataRequestPolicy.setRules(rules);

    DataRequestInput dataRequestInput = new DataRequestInput();
    dataRequestInput.setPolicy(dataRequestPolicy);
    dataRequestInput.setData(data);

    DataRequest dataRequest = new DataRequest();
    dataRequest.setInput(dataRequestInput);

    getJsonNode(dataRequest, "dataRequest");

    DataResponse dataResponse = openPolicyAgentClient.validateData(dataRequest);

    getJsonNode(dataResponse, "dataResponse");

    return dataResponse;
  }

  private static Integer getFaildedCount(PolicyActivityEntity activity) {
    Integer failCount = 0;
    for (Results results : activity.getResults()) {
      if (!results.getValid()) {
        failCount++;
      }
    }
    return failCount;
  }

  private static JsonNode getJsonNode(Object obj, String key) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode data = mapper.convertValue(obj, JsonNode.class);
    LOGGER.info("{}={}", key, getJsonNodeText(data));

    return data;
  }

  private static String getJsonNodeText(JsonNode node) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(node);
    } catch (JsonProcessingException e) {
      LOGGER.info(e);
    }
    return null;
  }

  private static Date fromLocalDate(LocalDate date) {
    return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  @Override
  public PolicyResponse deletePolicy(String policyId) {
    PolicyEntity policy = policyRepository.findById(policyId).orElse(null);
    PolicyResponse response = new PolicyResponse();
    /* TODO: Fix compilation errors. 
    if (getStagesForPolicy(policy.getTeamId(), policy.getId()).size() != 0) {
      response.setStatus(409);
      response.setMessage("Policy associated with gate");
      response.setError("Unable to delete");
      return response;
    } else {
      policyRepository.delete(policy);
      response.setStatus(200);
      response.setMessage("Policy deleted");
      response.setError("Policy deleted");
      return response;
    } */
    
    return null;
  }
}
