package net.boomerangplatform.service;

import java.time.Clock;
import java.time.LocalDateTime;
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
import net.boomerangplatform.entity.PolicyEntity;
import net.boomerangplatform.entity.PolicyTemplateEntity;
import net.boomerangplatform.exception.BosunError;
import net.boomerangplatform.exception.BosunException;
import net.boomerangplatform.model.Policy;
import net.boomerangplatform.model.PolicyActivitiesInsights;
import net.boomerangplatform.model.PolicyDefinition;
import net.boomerangplatform.model.PolicyInsights;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.PolicySummary;
import net.boomerangplatform.model.PolicyTemplate;
import net.boomerangplatform.model.PolicyValidation;
import net.boomerangplatform.model.PolicyValidationInput;
import net.boomerangplatform.model.PolicyViolation;
import net.boomerangplatform.model.PolicyViolations;
import net.boomerangplatform.model.Result;
import net.boomerangplatform.model.ResultViolation;
import net.boomerangplatform.model.Scope;
import net.boomerangplatform.model.Status;
import net.boomerangplatform.mongo.model.OperatorType;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataRequestInput;
import net.boomerangplatform.opa.model.DataRequestPolicy;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.model.DataResponseResultViolation;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;
import net.boomerangplatform.repository.PolicyActivityRepository;
import net.boomerangplatform.repository.PolicyRepository;
import net.boomerangplatform.repository.PolicyTemplateRepository;
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
  private PolicyTemplateRepository policyTemplateRepository;

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
  public List<PolicyTemplate> getAllTemplates() {
    List<PolicyTemplateEntity> entities =
        policyTemplateRepository.findAll(Sort.by(Sort.Direction.ASC, "order"));
    List<PolicyTemplate> descriptions = new ArrayList<>();

    entities.forEach(entity -> {
      PolicyTemplate description = new PolicyTemplate();
      BeanUtils.copyProperties(entity, description);
      descriptions.add(description);
    });

    return descriptions;
  }

  @Override
  public PolicyTemplate getTemplate(String templateId) {
    PolicyTemplateEntity entity = policyTemplateRepository.findById(templateId).orElse(null);
    PolicyTemplate template = new PolicyTemplate();
    if(entity != null) {
      BeanUtils.copyProperties(entity, template);
    }
    
    return template;
  }

  @Override
  public PolicyTemplate addTemplate(PolicyTemplate template) {
    template.setCreatedDate(new Date());

    PolicyTemplateEntity entity = new PolicyTemplateEntity();
    BeanUtils.copyProperties(template, entity);
    entity = policyTemplateRepository.insert(entity);
    template.setId(entity.getId());

    return template;
  }

  @Override
  public PolicyTemplate updateTemplate(String templateId, PolicyTemplate template) {
    PolicyTemplateEntity entity = policyTemplateRepository.findById(templateId).orElse(null);

    if(entity != null) {
      BeanUtils.copyProperties(template, entity);
      policyTemplateRepository.save(entity);
    }
    
    return template;
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

    entities.stream().filter(entity -> !entity.getStatus().equals(Status.inactive))
        .forEach(entity -> {
          Policy policy = new Policy();
          BeanUtils.copyProperties(entity, policy);
          policies.add(policy);
        });

    List<PolicyEntity> globalPolicies = policyRepository.findByScope(Scope.global);
    globalPolicies.stream().filter(entity -> !entity.getStatus().equals(Status.inactive))
        .forEach(entity -> {
          Policy policy = new Policy();
          BeanUtils.copyProperties(entity, policy);
          policies.add(policy);
        });

    return policies;
  }

  @Override
  public List<PolicySummary> getPoliciesSummaryByTeamId(String teamId) {
    List<PolicyEntity> entities = policyRepository.findByTeamId(teamId);
    List<PolicySummary> policySummaries = new ArrayList<>();

    entities.stream().filter(entity -> !entity.getStatus().equals(Status.inactive))
        .forEach(entity -> {
          PolicySummary policy = new PolicySummary();
          BeanUtils.copyProperties(entity, policy);
          policySummaries.add(policy);
        });

    List<PolicyEntity> globalPolicies = policyRepository.findByScope(Scope.global);
    globalPolicies.stream().filter(entity -> !entity.getStatus().equals(Status.inactive))
        .forEach(entity -> {
          PolicySummary policy = new PolicySummary();
          BeanUtils.copyProperties(entity, policy);
          policySummaries.add(policy);
        });

    return policySummaries;
  }

  @Override
  public Policy getPolicyById(String ciPolicyId) {
    PolicyEntity entity = policyRepository.findById(ciPolicyId).orElse(null);

    Policy policy = new Policy();
    if (entity != null) {
      BeanUtils.copyProperties(entity, policy);
    }

    return policy;
  }

  @Override
  public Policy addPolicy(Policy policy) {
    policy.setCreatedDate(new Date());

    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    policy.setScope(Scope.team);
    PolicyEntity entity = new PolicyEntity();
    BeanUtils.copyProperties(policy, entity);
    entity.setStatus(Status.active);
    entity = policyRepository.insert(entity);
    policy.setId(entity.getId());

    return policy;
  }


  @Override
  public Policy updatePolicy(Policy policy) {
    PolicyEntity entity = policyRepository.findById(policy.getId()).orElse(null);
    policy.setScope(Scope.team);
    policy.setDefinitions(getFilteredDefinition(policy.getDefinitions()));

    if (entity != null) {
      BeanUtils.copyProperties(policy, entity);
      policyRepository.save(entity);
    }

    return policy;
  }

  @Override
  public PolicyActivityEntity validatePolicy(PolicyValidation policyValidation) {

    PolicyEntity policyEntity =
        policyRepository.findById(policyValidation.getPolicyId()).orElse(null);

    if (policyEntity != null && policyEntity.getStatus().equals(Status.inactive)) {
      throw new BosunException(BosunError.POLICY_DELETED.getMessage(policyEntity.getId()));
    } else if (policyEntity != null) {
      final PolicyActivityEntity policiesActivities = new PolicyActivityEntity();
      policiesActivities.setTeamId(policyEntity.getTeamId());
      policiesActivities.setPolicyId(policyEntity.getId());
      policiesActivities.setLabels(policyValidation.getLabels());
      policiesActivities.setAnnotations(policyValidation.getAnnotations());
      policiesActivities.setReferenceLink(policyValidation.getReferenceLink());
      policiesActivities.setReferenceId(policyValidation.getReferenceId());
      policiesActivities.setCreatedDate(new Date());
      policiesActivities.setValid(true);

      List<Result> results = new ArrayList<>();

      if (policyEntity.getDefinitions() != null) {
        getPolicyDefinitions(policyValidation, policyEntity, policiesActivities, results);
      }

      policiesActivities.setResults(results);
      return policyActivityRepository.save(policiesActivities);
    } else {
      throw new BosunException(
          BosunError.POLICY_NOT_FOUND.getMessage(policyValidation.getPolicyId()));
    }
  }

  private void getPolicyDefinitions(PolicyValidation policyValidation, PolicyEntity policyEntity,
      final PolicyActivityEntity policiesActivities, List<Result> results) {
    policyEntity.getDefinitions().stream()
        .filter(policyTemplate -> !CollectionUtils.isEmpty(policyTemplate.getRules()))
        .forEach(policyTemplate -> {

          PolicyTemplateEntity policyTemplateEntity = policyTemplateRepository
              .findById(policyTemplate.getPolicyTemplateId()).orElse(null);

          PolicyValidationInput policyValidationInput = null;

          if (!policyValidation.getInputs().isEmpty()) {
            policyValidationInput = policyValidation.getInputs().stream()
                .filter(
                    input -> policyTemplate.getPolicyTemplateId().equals(input.getTemplateId()))
                .findFirst().get();
          }

          JsonNode data =
              policyValidationInput != null ? policyValidationInput.getData() : null;
          Result result = getResult(policyValidation.getLabels(), policyTemplate,
              policyTemplateEntity, data);

          if (result != null) {
            if (!result.getValid()) {
              policiesActivities.setValid(false);
              if (result.getViolations().isEmpty()) {
                ResultViolation resultViolation = new ResultViolation();
                resultViolation.setMetric(policyTemplateEntity.getName());
                resultViolation.setMessage("No data exists for component/version");
                resultViolation.setValid(false);
                result.getViolations().add(resultViolation);
              }
            }
            results.add(result);
          }
        });
  }

  @Override
  public PolicyValidation validateInfo(String policyId) {
    PolicyValidation policyInfo = new PolicyValidation();
    policyInfo.setPolicyId(policyId);
    PolicyEntity policy = policyRepository.findById(policyId).orElse(new PolicyEntity());
    List<PolicyValidationInput> policyInfoInputs = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.createObjectNode();
    policy.getDefinitions().stream()
        .filter(definition -> !CollectionUtils.isEmpty(definition.getRules()))
        .forEach(definition -> {
          PolicyValidationInput policyInfoInput = new PolicyValidationInput();
          policyInfoInput.setTemplateId(definition.getPolicyTemplateId());
          policyInfoInput.setData(jsonNode);
          policyInfoInputs.add(policyInfoInput);
        });
    policyInfo.setInputs(policyInfoInputs);
    Map<String, String> labels = new HashMap<>();
    policyTemplateRepository.findAll().forEach(policyTemplate -> {
      if (policyTemplate.getLabels() != null) {
        policyTemplate.getLabels().forEach(label -> labels.put(label, ""));
      }
    });
    policyInfo.setLabels(labels);
    policyInfo.setReferenceId("");
    policyInfo.setReferenceLink("");
    return policyInfo;
  }

  @Override
  public List<PolicyInsights> getInsights(String teamId) {
    Map<String, PolicyInsights> insights = new HashMap<>();
    LocalDateTime date =
        LocalDateTime.now(clock).minusMonths(Integer.valueOf(insightsPeriodMonths));

    List<PolicyActivityEntity> activities =
        policyActivityRepository.findByTeamIdAndValidAndCreatedDateAfter(teamId, false, date);

    for (PolicyActivityEntity activity : activities) {
      String policyId = activity.getPolicyId();

      PolicyInsights policyInsights = insights.get(policyId);
      if (policyInsights == null && policyRepository.findById(policyId).isPresent()) {
        Policy policy = getPolicyById(policyId);
        policyInsights = new PolicyInsights();
        policyInsights.setPolicyId(policy.getId());
        policyInsights.setPolicyName(policy.getName());
        policyInsights.setPolicyCreatedDate(policy.getCreatedDate());
      }

      PolicyActivitiesInsights policyActivitiesInsights = getPolicyActivitiesInsights(activity);

      if (policyInsights != null) {
        policyInsights.addInsights(policyActivitiesInsights);
        insights.put(policyId, policyInsights);
      }

    }

    return new ArrayList<>(insights.values());
  }

  private List<PolicyDefinition> getFilteredDefinition(List<PolicyDefinition> policyDefinitions) {
    List<PolicyDefinition> filteredDefinitions = new ArrayList<>();
    for (PolicyDefinition definition : policyDefinitions) {
      if (!definition.getRules().isEmpty()) {
        filteredDefinitions.add(definition);
      }
    }
    return filteredDefinitions;
  }

  private PolicyActivitiesInsights getPolicyActivitiesInsights(PolicyActivityEntity activity) {
    Integer failCount = getFaildedCount(activity);

    PolicyActivitiesInsights policyActivitiesInsights = null;

    policyActivitiesInsights = new PolicyActivitiesInsights();
    policyActivitiesInsights.setPolicyActivityId(activity.getReferenceId());
    policyActivitiesInsights.setPolicyActivityCreatedDate(activity.getCreatedDate());
    policyActivitiesInsights.setViolations(failCount);

    return policyActivitiesInsights;
  }

  @Override
  public List<PolicyViolations> getViolations(String teamId) {

    Map<String, PolicyViolations> violationsMap = new HashMap<>();

    LOGGER.info("team.id=" + teamId);

    List<PolicyEntity> policyEntities = policyRepository.findByTeamId(teamId);

    policyEntities.stream().filter(entity -> !entity.getStatus().equals(Status.inactive))
        .forEach(entity -> {
          LOGGER.info("policy.name=" + entity.getName());

          List<PolicyActivityEntity> policyActivityEntities = policyActivityRepository
              .findTopDistinctViolationsByPolicyIdAndReferenceId(entity.getId());

          LOGGER.info("policyActivities.size=" + policyActivityEntities.size());

          setViolations(violationsMap, policyActivityEntities);
        });

    return new ArrayList<>(violationsMap.values());
  }

  private void setViolations(Map<String, PolicyViolations> violationsMap,
      List<PolicyActivityEntity> policyActivities) {
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

      PolicyViolations violation =
          getViolation(key.toString(), policyActivity, policy, violationsMap.get(key.toString()));

      violationsMap.put(key.toString(), violation);
    }
  }

  private PolicyViolations getViolation(String key, PolicyActivityEntity policyActivity,
      PolicyEntity policy, PolicyViolations violation) {

    if (violation == null) {
      violation = new PolicyViolations();
      violation.setId(key);
      violation.setPolicyId(policy.getId());
      violation.setPolicyName(policy.getName());
      violation.setReferenceId(policyActivity.getReferenceId());
      violation.setReferenceLink(policyActivity.getReferenceLink());
      violation.setLabels(policyActivity.getLabels());
      violation.setAnnotations(policyActivity.getAnnotations());
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
    for (Result result : policyActivity.getResults()) {
      if (!result.getValid() && !result.getViolations().isEmpty()) {
        resultsViolations.addAll(getPolicyViolation(result.getViolations()));
      }
    }
    return resultsViolations;
  }

  private List<PolicyViolation> getPolicyViolation(List<ResultViolation> violations) {
    List<PolicyViolation> policyViolations = new ArrayList<>();
    for (ResultViolation resultsViolation : violations) {
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
    for (Result result : policyActivity.getResults()) {
      if (!result.getValid()) {
        violationsTotal++;
      }
    }
    return violationsTotal;
  }

  private List<String> getViolationsDefinitionTypes(List<String> current,
      PolicyActivityEntity policyActivity) {
    List<String> violationsDefinitionTypes = new ArrayList<>();
    for (Result result : policyActivity.getResults()) {
      if (!result.getValid()) {
        String policyTemplateId =
            result.getPolicyTemplateId() != null ? result.getPolicyTemplateId() : "";
        LOGGER.info("policyTemplateId=" + policyTemplateId);
        PolicyTemplateEntity policyTemplateEntity =
            policyTemplateRepository.findById(policyTemplateId).orElse(null);
        if (policyTemplateEntity != null && !current.contains(policyTemplateEntity.getName())) {
          LOGGER.info("policyDefinitionName=" + policyTemplateEntity.getName());
          violationsDefinitionTypes.add(policyTemplateEntity.getName());
        }
      }
    }
    return violationsDefinitionTypes;
  }

  private Result getResult(Map<String, String> labels, PolicyDefinition policyDefinition,
      PolicyTemplateEntity policyTemplate, JsonNode data) {

    if (policyTemplate == null) {
      return null;
    }

    Result result = getDefaultResult(policyTemplate.getId());
    String key = policyTemplate.getKey().toLowerCase(Locale.US);
    if (data != null) {
      LOGGER.info(data);
      result = getResult(policyTemplate, policyDefinition, data);
    } else {
      switch (key) {
        case "static_code_analysis":
          SonarQubeReport sonarQubeReport = repositoryService
              .getSonarQubeReport(labels.get("sonarqube-id"), labels.get("sonarqube-version"));
          result = getResult(policyTemplate, policyDefinition, getJsonNode(sonarQubeReport, key));
          break;
        case "unit_tests":
          SonarQubeReport sonarQubeTestCoverage = repositoryService.getSonarQubeTestCoverage(
              labels.get("sonarqube-id"), labels.get("sonarqube-version"));
          result =
              getResult(policyTemplate, policyDefinition, getJsonNode(sonarQubeTestCoverage, key));
          break;
        case "package_safelist":
          DependencyGraph dependencyGraph =
              repositoryService.getDependencyGraph(labels.get("artifact-path"),
                  labels.get("artifact-name"), labels.get("artifact-version"));
          result = getResult(policyTemplate, policyDefinition, getJsonNode(dependencyGraph, key));
          break;
        case "cve_safelist":
        case "security_issue_analysis":
          ArtifactSummary summary =
              repositoryService.getArtifactSummary(labels.get("artifact-path"),
                  labels.get("artifact-name"), labels.get("artifact-version"));
          if (!summary.getArtifacts().isEmpty()) {
            result = getResult(policyTemplate, policyDefinition,
                getJsonNode(summary.getArtifacts().get(0).getIssues(), key));
          }
          break;
        default:
          result = null;
          break;
      }
    }
    return result;
  }

  private Result getDefaultResult(String policyTemplateId) {
    Result result = new Result();
    result.setPolicyTemplateId(policyTemplateId);
    result.setViolations(new ArrayList<ResultViolation>());
    result.setValid(false);

    return result;
  }

  private Result getResult(PolicyTemplateEntity policyTemplateEntity,
      PolicyDefinition policyDefinition, JsonNode data) {

    DataResponse dataResponse = callOpenPolicyAgentClient(policyTemplateEntity.getId(),
        policyTemplateEntity.getKey(), policyDefinition.getRules(), data);

    Result result = new Result();
    result.setPolicyTemplateId(policyTemplateEntity.getId());
    result.setViolations(getResultsViolation(dataResponse.getResult().getViolations()));
    result.setValid(dataResponse.getResult().getValid());

    return result;
  }

  private List<ResultViolation> getResultsViolation(
      List<DataResponseResultViolation> dataResponseResultViolations) {
    List<ResultViolation> resultsViolations = new ArrayList<>();
    for (DataResponseResultViolation dataResponseResultViolation : dataResponseResultViolations) {
      ResultViolation resultsViolation = new ResultViolation();
      resultsViolation.setMessage(dataResponseResultViolation.getMessage());
      resultsViolation.setMetric(dataResponseResultViolation.getMetric());
      resultsViolation.setValid(dataResponseResultViolation.getValid());
      resultsViolations.add(resultsViolation);
    }
    return resultsViolations;
  }

  private DataResponse callOpenPolicyAgentClient(String policyTemplateId, String policyTemplateKey,
      List<Map<String, String>> rules, JsonNode data) {

    DataRequestPolicy dataRequestPolicy = new DataRequestPolicy();
    dataRequestPolicy.setId(policyTemplateId);
    dataRequestPolicy.setKey(policyTemplateKey);
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
    return (int) activity.getResults().stream().filter(result -> !result.getValid()).count();
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

  @Override
  public PolicyResponse deletePolicy(String policyId) {
    PolicyEntity policy = policyRepository.findById(policyId).orElse(null);
    PolicyResponse response = new PolicyResponse();

    if (policy != null) {
      policy.setStatus(Status.inactive);
      policyRepository.save(policy);
      response.setStatus(200);
      response.setMessage("Policy deleted");
      response.setError("Policy deleted");
      return response;
    } else {
      response.setStatus(409);
      response.setMessage("Unable to mark inactive");
      response.setError("Unable to mark inactive");
      return response;
    }
  }
}
