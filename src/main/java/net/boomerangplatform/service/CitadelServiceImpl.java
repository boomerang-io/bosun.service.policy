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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyActivitiesInsights;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.model.EventStatus;
import net.boomerangplatform.mongo.entity.CiComponentActivityEntity;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiPipelineEntity;
import net.boomerangplatform.mongo.entity.CiPolicyActivityEntity;
import net.boomerangplatform.mongo.entity.CiPolicyDefinitionEntity;
import net.boomerangplatform.mongo.entity.CiPolicyEntity;
import net.boomerangplatform.mongo.entity.CiStageEntity;
import net.boomerangplatform.mongo.model.CiComponentActivityType;
import net.boomerangplatform.mongo.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.mongo.model.Results;
import net.boomerangplatform.mongo.model.Scope;
import net.boomerangplatform.mongo.service.CiComponentActivityService;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
import net.boomerangplatform.mongo.service.CiPipelineService;
import net.boomerangplatform.mongo.service.CiPolicyActivityService;
import net.boomerangplatform.mongo.service.CiPolicyDefinitionService;
import net.boomerangplatform.mongo.service.CiPolicyService;
import net.boomerangplatform.mongo.service.CiStagesService;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataRequestInput;
import net.boomerangplatform.opa.model.DataRequestPolicy;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;
import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;
import net.boomerangplatform.repository.service.RepositoryService;

@Service
public class CitadelServiceImpl implements CitadelService {

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
  private CiPolicyService ciPolicyService;

  @Autowired
  private CiPolicyDefinitionService ciPolicyDefinitionService;

  @Autowired
  private CiPolicyActivityService ciPolicyActivityService;

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
  public List<CiPolicy> getPoliciesByTeamId(String ciTeamId) {
    List<CiPolicyEntity> entities = ciPolicyService.findByTeamId(ciTeamId);
    List<CiPolicy> policies = new ArrayList<>();

    entities.forEach(entity -> {
      CiPolicy policy = new CiPolicy();
      BeanUtils.copyProperties(entity, policy);
      policy.setStages(getStagesForPolicy(ciTeamId, entity.getId()));
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
    policy.setCreatedDate(fromLocalDate(LocalDate.now(clock)));

    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    policy.setScope(Scope.team);
    CiPolicyEntity entity = new CiPolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity = ciPolicyService.add(entity);
    policy.setId(entity.getId());

    return policy;
  }


  @Override
  public CiPolicy updatePolicy(CiPolicy policy) {
    CiPolicyEntity entity = ciPolicyService.findById(policy.getId());
    policy.setScope(Scope.team);
    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    BeanUtils.copyProperties(policy, entity);
    ciPolicyService.update(entity);

    return policy;
  }

  @Override
  public CiPolicyActivityEntity validatePolicy(String ciComponentActivityId, String ciPolicyId) {

    CiComponentActivityEntity ciComponentActivityEntity =
        ciComponentActivityService.findById(ciComponentActivityId);
    CiComponentEntity ciComponentEntity =
        ciComponentService.findById(ciComponentActivityEntity.getCiComponentId());
    CiComponentVersionEntity ciComponentVersionEntity = ciComponentVersionService
        .findVersionWithId(ciComponentActivityEntity.getCiComponentVersionId());

    final CiPolicyActivityEntity policiesActivities = new CiPolicyActivityEntity();
    policiesActivities.setCiTeamId(ciComponentEntity.getCiTeamId());
    policiesActivities.setCiComponentActivityId(ciComponentActivityEntity.getId());
    policiesActivities.setCiPolicyId(ciPolicyId);
    policiesActivities.setCreatedDate(new Date());
    policiesActivities.setValid(true);

    List<Results> results = new ArrayList<>();


    CiPolicyEntity policyEntity = ciPolicyService.findById(ciPolicyId);

    if (policyEntity != null && policyEntity.getDefinitions() != null) {
      policyEntity.getDefinitions().stream()
          .filter(policyConfig -> !CollectionUtils.isEmpty(policyConfig.getRules())).forEach(policyConfig -> {
            
            CiPolicyDefinitionEntity policyDefinitionEntity =
                ciPolicyDefinitionService.findById(policyConfig.getCiPolicyDefinitionId());

            Results result = getResult(ciComponentEntity.getId(),
                ciComponentVersionEntity.getName(), policyConfig, policyDefinitionEntity);

            if (result != null) {
              if (!result.getValid()) {
                policiesActivities.setValid(false);
              }

              results.add(result);
            }
          });
    }

    policiesActivities.setResults(results);
    return ciPolicyActivityService.save(policiesActivities);

  }

  @Override
  public List<CiPolicyInsights> getInsights(String ciTeamId) {
    Map<String, CiPolicyInsights> insights = new HashMap<>();
    LocalDate date = LocalDate.now(clock).minusMonths(Integer.valueOf(insightsPeriodMonths));

    List<CiPolicyActivityEntity> activities = ciPolicyActivityService
        .findByCiTeamIdAndValidAndCreatedDateAfter(ciTeamId, false, fromLocalDate(date));

    for (CiPolicyActivityEntity activity : activities) {
      String ciPolicyId = activity.getCiPolicyId();

      CiPolicyInsights ciPolicyInsights = insights.get(ciPolicyId);
      if (ciPolicyInsights == null) {
        CiPolicy ciPolicy = getPolicyById(ciPolicyId);

        ciPolicyInsights = new CiPolicyInsights();
        ciPolicyInsights.setCiPolicyId(ciPolicy.getId());
        ciPolicyInsights.setCiPolicyName(ciPolicy.getName());
        ciPolicyInsights.setCiPolicyCreatedDate(ciPolicy.getCreatedDate());
      }

      CiPolicyActivitiesInsights ciPolicyActivitiesInsights =
          getCiPolicyActivitiesInsights(activity, ciPolicyInsights);

      ciPolicyInsights.addInsights(ciPolicyActivitiesInsights);
      insights.put(ciPolicyId, ciPolicyInsights);
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

    for (CiPolicyActivitiesInsights activites : ciPolicyInsights.getInsights()) {
      if (activites.getCiPolicyActivityId().equalsIgnoreCase(activity.getCiComponentActivityId())) {
        ciPolicyActivitiesInsights = activites;
        ciPolicyInsights.removeInsights(activites);
        break;
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

        List<CiPolicyActivityEntity> policyActivities =
            ciPolicyActivityService.findByCiComponentActivityId(componentActivity.getId());

        LOGGER.info("policyActivities.size=" + policyActivities.size());

        setViolations(violationsMap, component, stage, componentActivity, policyActivities);
      }
    }

    return new ArrayList<>(violationsMap.values());
  }

  private void setViolations(Map<String, CiPolicyViolations> violationsMap,
      CiComponentEntity component, CiStageEntity stage, CiComponentActivityEntity componentActivity,
      List<CiPolicyActivityEntity> policyActivities) {
    for (CiPolicyActivityEntity policyActivity : policyActivities) {
      CiPolicyEntity policy = ciPolicyService.findById(policyActivity.getCiPolicyId());

      if (policy == null) {
        continue;
      }

      LOGGER.info("policy.name=" + policy.getName());

      CiComponentVersionEntity componentVersion =
          ciComponentVersionService.findVersionWithId(componentActivity.getCiComponentVersionId());

      LOGGER.info("componentVersion.name=" + componentVersion.getName());

      StringBuilder key = new StringBuilder();
      key.append(policy.getId()).append(component.getId()).append(componentVersion.getId())
          .append(stage.getId());

      LOGGER.info("key=" + key.toString());

      CiPolicyViolations violation = getViolation(key.toString(), component, stage, policyActivity, policy,
          componentVersion, violationsMap.get(key.toString()));

      violationsMap.put(key.toString(), violation);
    }
  }

  private CiPolicyViolations getViolation(String key, CiComponentEntity component, CiStageEntity stage,
      CiPolicyActivityEntity policyActivity, CiPolicyEntity policy,
      CiComponentVersionEntity componentVersion, CiPolicyViolations violation) {

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
      violation.setViolations(0);
      violation.setCiPolicyActivityCreatedDate(policyActivity.getCreatedDate());
    } else if (policyActivity.getCreatedDate().after(violation.getCiPolicyActivityCreatedDate())) {
      violation.setViolations(0);
      violation.setCiPolicyActivityCreatedDate(policyActivity.getCreatedDate());
    }

    violation.setViolations(violation.getViolations() + getViolationsTotal(policyActivity));

    return violation;
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
          result = getResults(policyDefinition, policyConfig, getJsonNode(sonarQubeTestCoverage, key));
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
    result.setDetail(null);
    result.setValid(false);

    return result;
  }

  private Results getResults(CiPolicyDefinitionEntity policyDefinitionEntity,
      CiPolicyConfig policyConfig, JsonNode data) {

    DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
        policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

    Results result = new Results();
    result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
    result.setDetail(getJsonNodeText(dataResponse.getResult().getDetail()));
    result.setValid(dataResponse.getResult().getValid());

    return result;
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

  @Override
  public EventStatus deletePolicy(String ciPolicyId) {
    CiPolicyEntity ciPolicy = ciPolicyService.findById(ciPolicyId);
    
    EventStatus eventStatus = new EventStatus();
    eventStatus.setId(ciPolicy.getId());
    eventStatus.setName(ciPolicy.getName());
    
    if (ciPolicy.getTeamId() == null) {
      eventStatus.setStatus(HttpStatus.NOT_ACCEPTABLE);
      eventStatus.setDescription("Unable to delete - Policy associated with team");
      return eventStatus;
    }else {
      ciPolicyService.delete(ciPolicy);
      eventStatus.setStatus(HttpStatus.OK);
      eventStatus.setDescription("Policy deleted");
      return eventStatus;
    }
  }
}
