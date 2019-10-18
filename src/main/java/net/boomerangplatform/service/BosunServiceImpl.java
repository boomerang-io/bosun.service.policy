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

import net.boomerangplatform.entity.CiPolicyActivityEntity;
import net.boomerangplatform.entity.CiPolicyDefinitionEntity;
import net.boomerangplatform.entity.CiPolicyEntity;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyActivitiesInsights;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolation;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.Results;
import net.boomerangplatform.model.ResultsViolation;
import net.boomerangplatform.mongo.entity.CiComponentActivityEntity;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiPipelineEntity;
import net.boomerangplatform.mongo.entity.CiStageEntity;
import net.boomerangplatform.mongo.model.CiComponentActivityType;
import net.boomerangplatform.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.mongo.model.Scope;
import net.boomerangplatform.mongo.service.CiComponentActivityService;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
import net.boomerangplatform.mongo.service.CiPipelineService;
import net.boomerangplatform.mongo.service.CiStagesService;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataRequestInput;
import net.boomerangplatform.opa.model.DataRequestPolicy;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.model.DataResponseResultViolation;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;
import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;
import net.boomerangplatform.repository.service.RepositoryService;

@Service
public class BosunServiceImpl implements BosunService {

  @Value("${insights.period.months}")
  private String insightsPeriodMonths;

  @Autowired
  private CiComponentActivityService ciComponentActivityService;

  @Autowired
  private CiComponentService ciComponentService;

  @Autowired
  private CiComponentVersionService ciComponentVersionService;

  @Autowired
  private CiPipelineService ciPipelineService;

  @Autowired
  private CiStagesService ciStagesService;

  @Autowired
  private CiPolicyRepository ciPolicyRepository;

  @Autowired
  private CiPolicyDefinitionRepository ciPolicyDefinitionRepository;

  @Autowired
  private CiPolicyActivityRepository ciPolicyActivityRepository;

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
  public List<CiPolicyDefinition> getAllDefinitions() {
    List<CiPolicyDefinitionEntity> entities = ciPolicyDefinitionRepository.findAll(new Sort(Sort.Direction.ASC, "order"));
    List<CiPolicyDefinition> descriptions = new ArrayList<>();

    entities.forEach(entity -> {
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
  public List<CiPolicy> getPoliciesByTeamId(String ciTeamId) {
    List<CiPolicyEntity> entities = ciPolicyRepository.findByTeamId(ciTeamId);
    List<CiPolicy> policies = new ArrayList<>();

    entities.forEach(entity -> {
      CiPolicy policy = new CiPolicy();
      BeanUtils.copyProperties(entity, policy);
      policy.setStages(getStagesForPolicy(ciTeamId, entity.getId()));
      policies.add(policy);
    });

    List<CiPolicyEntity> globalPolicies = ciPolicyRepository.findByScope(Scope.global);
    globalPolicies.forEach(entity -> {
      CiPolicy policy = new CiPolicy();
      BeanUtils.copyProperties(entity, policy);
      policy.setStages(getStagesForGlobalPolicy(entity.getId()));
      policies.add(policy);
    });

    return policies;
  }

  @Override
  public CiPolicy getPolicyById(String ciPolicyId) {
    CiPolicyEntity entity = ciPolicyRepository.findById(ciPolicyId).orElse(null);

    CiPolicy policy = new CiPolicy();
    BeanUtils.copyProperties(entity, policy);

    return policy;
  }

  @Override
  public CiPolicy addPolicy(CiPolicy policy) {
    policy.setCreatedDate(fromLocalDate(LocalDate.now(clock())));

    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    policy.setScope(Scope.team);
    CiPolicyEntity entity = new CiPolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity = ciPolicyRepository.insert(entity);
    policy.setId(entity.getId());

    return policy;
  }


  @Override
  public CiPolicy updatePolicy(CiPolicy policy) {
    CiPolicyEntity entity = ciPolicyRepository.findById(policy.getId()).orElse(null);
    policy.setScope(Scope.team);
    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    BeanUtils.copyProperties(policy, entity);
    ciPolicyRepository.save(entity);

    return policy;
  }

	@Override
	public CiPolicyActivityEntity validatePolicy(String ciPolicyId, String ciComponentActivityId, String ciComponentId, String ciComponentVersion) {

		CiPolicyEntity policyEntity = ciPolicyRepository.findById(ciPolicyId).orElse(null);

		final CiPolicyActivityEntity policiesActivities = new CiPolicyActivityEntity();
		policiesActivities.setCiTeamId(policyEntity.getTeamId());
		policiesActivities.setCiComponentActivityId(ciComponentActivityId);
		policiesActivities.setCiPolicyId(ciPolicyId);
		policiesActivities.setCreatedDate(fromLocalDate(LocalDate.now(clock)));
		policiesActivities.setValid(true);

		List<Results> results = new ArrayList<>();

		if (policyEntity != null && policyEntity.getDefinitions() != null) {
			policyEntity.getDefinitions().stream()
					.filter(policyConfig -> !CollectionUtils.isEmpty(policyConfig.getRules())).forEach(policyConfig -> {

						CiPolicyDefinitionEntity policyDefinitionEntity = ciPolicyDefinitionRepository
								.findById(policyConfig.getCiPolicyDefinitionId()).orElse(null);

						Results result = getResult(ciComponentId, ciComponentVersion,
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
		return ciPolicyActivityRepository.save(policiesActivities);

	}

  @Override
  public List<CiPolicyInsights> getInsights(String ciTeamId) {
    Map<String, CiPolicyInsights> insights = new HashMap<>();
    LocalDate date = LocalDate.now(clock).minusMonths(Integer.valueOf(insightsPeriodMonths));

    List<CiPolicyActivityEntity> activities = ciPolicyActivityRepository
        .findByCiTeamIdAndValidAndCreatedDateAfter(ciTeamId, false, fromLocalDate(date));

    for (CiPolicyActivityEntity activity : activities) {
      String ciPolicyId = activity.getCiPolicyId();

      CiPolicyInsights ciPolicyInsights = insights.get(ciPolicyId);
      if (ciPolicyInsights == null && ciPolicyRepository.findById(ciPolicyId).isPresent()) {
        CiPolicy ciPolicy = getPolicyById(ciPolicyId);
        ciPolicyInsights = new CiPolicyInsights();
        ciPolicyInsights.setCiPolicyId(ciPolicy.getId());
        ciPolicyInsights.setCiPolicyName(ciPolicy.getName());
        ciPolicyInsights.setCiPolicyCreatedDate(ciPolicy.getCreatedDate());
      }

      CiPolicyActivitiesInsights ciPolicyActivitiesInsights =
          getCiPolicyActivitiesInsights(activity, ciPolicyInsights);

      if(ciPolicyInsights != null) {
      ciPolicyInsights.addInsights(ciPolicyActivitiesInsights);
      insights.put(ciPolicyId, ciPolicyInsights);
      }
     
    }

    return new ArrayList<>(insights.values());
  }

  private List<CiPolicyConfig> getFilteredDefinition(List<CiPolicyConfig> policyDefinitions) {
    List<CiPolicyConfig> filteredDefinitions = new ArrayList<>();
    for (CiPolicyConfig definition : policyDefinitions) {
      if (!definition.getRules().isEmpty()) {
        filteredDefinitions.add(definition);
      }
    }
    return filteredDefinitions;
  }

  private CiPolicyActivitiesInsights getCiPolicyActivitiesInsights(CiPolicyActivityEntity activity,
      CiPolicyInsights ciPolicyInsights) {
    Integer failCount = getFaildedCount(activity);

    CiPolicyActivitiesInsights ciPolicyActivitiesInsights = null;

    if(ciPolicyInsights != null) {
    for (CiPolicyActivitiesInsights activites : ciPolicyInsights.getInsights()) {
      if (activites.getCiPolicyActivityId().equalsIgnoreCase(activity.getCiComponentActivityId())) {
        ciPolicyActivitiesInsights = activites;
        ciPolicyInsights.removeInsights(activites);
        break;
      }
    }
    }

    if (ciPolicyActivitiesInsights == null) {
      ciPolicyActivitiesInsights = new CiPolicyActivitiesInsights();
      ciPolicyActivitiesInsights.setCiPolicyActivityId(activity.getCiComponentActivityId());
      ciPolicyActivitiesInsights.setCiPolicyActivityCreatedDate(activity.getCreatedDate());
      ciPolicyActivitiesInsights.setViolations(failCount);
    } else {
      ciPolicyActivitiesInsights
          .setViolations(ciPolicyActivitiesInsights.getViolations() + failCount);
    }
    return ciPolicyActivitiesInsights;
  }

	@Override
	public List<CiPolicyViolations> getViolations(String ciTeamId) {

		List<CiPipelineEntity> pipelines = ciPipelineService.findByCiTeamId(ciTeamId);
		List<CiStageEntity> stagesWithGates = getStagesWithGates(pipelines);

		LOGGER.info("stagesWithGates.count=" + stagesWithGates.size());

		Map<String, CiPolicyViolations> violationsMap = new HashMap<>();

		List<CiComponentEntity> components = ciComponentService.findByCiTeamId(ciTeamId);

		for (CiComponentEntity component : components) {

			LOGGER.info("component.name=" + component.getName());

			for (CiStageEntity stage : stagesWithGates) {
				CiComponentActivityEntity componentActivity = ciComponentActivityService
						.findTopByCiComponentIdAndTypeAndCiStageIdOrderByCreationDateDesc(component.getId(),
								CiComponentActivityType.GATES, stage.getId());
				
				if (componentActivity == null) {
					continue;
				}

				LOGGER.info("componentActivity.id=" + componentActivity.getId());

				List<CiPolicyActivityEntity> policyActivities = ciPolicyActivityRepository
						.findByCiComponentActivityIdAndValid(componentActivity.getId(), false);

				LOGGER.info("policyActivities.size=" + policyActivities.size());

				setViolations(violationsMap, component, stage, componentActivity, policyActivities);
			}
		}

		return new ArrayList<>(violationsMap.values());
	}

	private void setViolations(Map<String, CiPolicyViolations> violationsMap, CiComponentEntity component,
			CiStageEntity stage, CiComponentActivityEntity componentActivity,
			List<CiPolicyActivityEntity> policyActivities) {
		for (CiPolicyActivityEntity policyActivity : policyActivities) {
			CiPolicyEntity policy = ciPolicyRepository.findById(policyActivity.getCiPolicyId()).orElse(null);
			
			if (policy == null) {
				continue;
			}

			LOGGER.info("policy.name=" + policy.getName());

			CiComponentVersionEntity componentVersion = ciComponentVersionService
					.findVersionWithId(componentActivity.getCiComponentVersionId());

			LOGGER.info("componentVersion.name=" + componentVersion.getName());

			StringBuilder key = new StringBuilder();
			key.append(policy.getId()).append(component.getId()).append(componentVersion.getId()).append(stage.getId());

			LOGGER.info("key=" + key.toString());

			CiPolicyViolations violation = getViolation(key.toString(), component, stage, policyActivity, policy,
					componentVersion, violationsMap.get(key.toString()));

			violationsMap.put(key.toString(), violation);
		}
	}

	private CiPolicyViolations getViolation(String key, CiComponentEntity component, CiStageEntity stage,
			CiPolicyActivityEntity policyActivity, CiPolicyEntity policy, CiComponentVersionEntity componentVersion,
			CiPolicyViolations violation) {

		if (violation == null) {
			violation = new CiPolicyViolations();
			violation.setId(key);
			violation.setCiComponentId(component.getId());
			violation.setCiComponentName(component.getName());
			violation.setCiComponentVersionId(componentVersion.getId());
			violation.setCiComponentVersionName(componentVersion.getName());
			violation.setCiPolicyId(policy.getId());
			violation.setCiPolicyName(policy.getName());
			violation.setCiStageId(stage.getId());
			violation.setCiStageName(stage.getName());
			violation.setNbrViolations(0);
			violation.setViolations(null);
			violation.setCiPolicyActivityCreatedDate(policyActivity.getCreatedDate());
		} else if (policyActivity.getCreatedDate().after(violation.getCiPolicyActivityCreatedDate())) {
			violation.setNbrViolations(0);
			violation.setViolations(null);
			violation.setCiPolicyActivityCreatedDate(policyActivity.getCreatedDate());
		}

		violation.setNbrViolations(violation.getNbrViolations() + getViolationsTotal(policyActivity));
		violation.getViolations().addAll(getViolationsResults(policyActivity));
		violation.getCiPolicyDefinitionTypes()
				.addAll(getViolationsDefinitionTypes(violation.getCiPolicyDefinitionTypes(), policyActivity));

		return violation;
	}
  
  private List<CiPolicyViolation> getViolationsResults(CiPolicyActivityEntity policyActivity) {
	  List<CiPolicyViolation> resultsViolations = new ArrayList<CiPolicyViolation>();
	  for (Results results : policyActivity.getResults()) {
		  if (!results.getValid()) {
			  if (!results.getViolations().isEmpty()) {
				  resultsViolations.addAll(getCiPolicyViolations(results.getViolations()));	  
			  }			  	  
		  }		  
	  }	  
	  return resultsViolations;
  }
  
  private List<CiPolicyViolation> getCiPolicyViolations(List<ResultsViolation> violations) {
	  List<CiPolicyViolation> policyViolations = new ArrayList<CiPolicyViolation>();
	  for (ResultsViolation resultsViolation : violations) {
		  CiPolicyViolation policyViolation = new CiPolicyViolation();
		  policyViolation.setMetric(resultsViolation.getMetric());
		  policyViolation.setMessage(resultsViolation.getMessage());
		  policyViolation.setValid(resultsViolation.getValid());
		  policyViolations.add(policyViolation);
	  }
	  return policyViolations;
  }

  private Integer getViolationsTotal(CiPolicyActivityEntity policyActivity) {
    Integer violationsTotal = 0;
    for (Results result : policyActivity.getResults()) {
      if (!result.getValid()) {
        violationsTotal++;
      }
    }
    return violationsTotal;
  }
  
	private List<String> getViolationsDefinitionTypes(List<String> current, CiPolicyActivityEntity policyActivity) {
		List<String> violationsDefinitionTypes = new ArrayList<String>();
		for (Results result : policyActivity.getResults()) {
			if (!result.getValid()) {
				CiPolicyDefinitionEntity policyDefinitionEntity = ciPolicyDefinitionRepository.findById(result.getCiPolicyDefinitionId()).orElse(null);
				if (!current.contains(policyDefinitionEntity.getName())) {
					violationsDefinitionTypes.add(policyDefinitionEntity.getName());
				}
			}
		}		
		return violationsDefinitionTypes;
	}

  private List<CiStageEntity> getStagesWithGates(List<CiPipelineEntity> pipelines) {
    List<CiStageEntity> stagesWithGates = new ArrayList<>();
    for (CiPipelineEntity pipeline : pipelines) {
      List<CiStageEntity> stages = ciStagesService.findByPipelineId(pipeline.getId());
      for (CiStageEntity stage : stages) {
        if (stage.getGates() != null && stage.getGates().getEnabled()) {
          stagesWithGates.add(stage);
        }
      }
    }
    return stagesWithGates;
  }

  private Results getResult(String componentId, String versionName, CiPolicyConfig policyConfig,
      CiPolicyDefinitionEntity policyDefinition) {

    if (policyDefinition == null) {
      return null;
    }

    Results result = getDefaultResult(policyDefinition.getId());
    String key = policyDefinition.getKey().toLowerCase(Locale.US);
    switch (key) {
      case "static_code_analysis":
        SonarQubeReport sonarQubeReport =
            repositoryService.getSonarQubeReport(componentId, versionName);
        result = getResults(policyDefinition, policyConfig, getJsonNode(sonarQubeReport, key));
        break;
      case "unit_tests":
        SonarQubeReport sonarQubeTestCoverage =
            repositoryService.getSonarQubeTestCoverage(componentId, versionName);
        result =
            getResults(policyDefinition, policyConfig, getJsonNode(sonarQubeTestCoverage, key));
        break;
      case "package_safelist":
        DependencyGraph dependencyGraph =
            repositoryService.getDependencyGraph(componentId, versionName);
        result = getResults(policyDefinition, policyConfig, getJsonNode(dependencyGraph, key));
        break;
      case "cve_safelist":
      case "security_issue_analysis":
        ArtifactSummary summary = repositoryService.getArtifactSummary(componentId, versionName);
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
    result.setCiPolicyDefinitionId(policyDefinitionId);
    result.setViolations(new ArrayList<ResultsViolation>());
    result.setValid(false);

    return result;
  }

  private Results getResults(CiPolicyDefinitionEntity policyDefinitionEntity,
      CiPolicyConfig policyConfig, JsonNode data) {

    DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
        policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

    Results result = new Results();
    result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());    
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

  private static Integer getFaildedCount(CiPolicyActivityEntity activity) {
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

  private List<String> getStagesForPolicy(String ciTeamId, String ciPolicyId) {
    List<String> stagesForPolicy = new ArrayList<>();

    List<CiPipelineEntity> pipelines = ciPipelineService.findByCiTeamId(ciTeamId);
    for (CiPipelineEntity pipeline : pipelines) {
      List<CiStageEntity> stages = ciStagesService.findByPipelineId(pipeline.getId());
      for (CiStageEntity stage : stages) {
        if (stage.getGates() != null && stage.getGates().getEnabled()
            && stage.getGates().getPolicies().contains(ciPolicyId)) {
          stagesForPolicy.add(stage.getName());
        }
      }
    }

    return stagesForPolicy;
  }

  private List<String> getStagesForGlobalPolicy(String ciPolicyId) {
    List<String> stagesForGlobalPolicy = new ArrayList<>();
    List<CiPipelineEntity> pipelines = ciPipelineService.getAllPipelines();
    for (CiPipelineEntity pipeline : pipelines) {
      List<CiStageEntity> stages = ciStagesService.findByPipelineId(pipeline.getId());
      for (CiStageEntity stage : stages) {
        if (stage.getGates() != null && stage.getGates().getEnabled()
            && stage.getGates().getPolicies().contains(ciPolicyId)) {
          stagesForGlobalPolicy.add(stage.getName());
        }
      }
    }
    return stagesForGlobalPolicy;
  }

  @Override
  public PolicyResponse deletePolicy(String ciPolicyId) {
    CiPolicyEntity ciPolicy = ciPolicyRepository.findById(ciPolicyId).orElse(null);
    PolicyResponse response = new PolicyResponse();
    if (getStagesForPolicy(ciPolicy.getTeamId(), ciPolicy.getId()).size() != 0) {
      response.setStatus(409);
      response.setMessage("Policy associated with gate");
      response.setError("Unable to delete");
      return response;
    } else {
      ciPolicyRepository.delete(ciPolicy);
      response.setStatus(200);
      response.setMessage("Policy deleted");
      response.setError("Policy deleted");
      return response;
    }
  }
}