package net.boomerangplatform.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyActivitiesInsights;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
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

	private static final Logger LOGGER = LogManager.getLogger();

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
		policy.setCreatedDate(new Date());
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
	public CiPolicyActivityEntity validatePolicy(String ciComponentActivityId, String ciPolicyId) {
		
		CiComponentActivityEntity ciComponentActivityEntity = ciComponentActivityService.findById(ciComponentActivityId);		
		CiComponentEntity ciComponentEntity = ciComponentService.findById(ciComponentActivityEntity.getCiComponentId());
		CiComponentVersionEntity ciComponentVersionEntity = ciComponentVersionService.findVersionWithId(ciComponentActivityEntity.getCiComponentVersionId());		

		CiPolicyActivityEntity policiesActivities = new CiPolicyActivityEntity();
		policiesActivities.setCiTeamId(ciComponentEntity.getCiTeamId());
		policiesActivities.setCiComponentActivityId(ciComponentActivityEntity.getId());
		policiesActivities.setCiPolicyId(ciPolicyId);
		policiesActivities.setCreatedDate(new Date());
		policiesActivities.setValid(false);

		policiesActivities = ciPolicyActivityService.save(policiesActivities);

		List<Results> results = new ArrayList<>();

		boolean overallResult = true;

		CiPolicyEntity policyEntity = ciPolicyService.findById(ciPolicyId);
		for (CiPolicyConfig policyConfig : policyEntity.getDefinitions()) {

			CiPolicyDefinitionEntity policyDefinitionEntity = ciPolicyDefinitionService
					.findById(policyConfig.getCiPolicyDefinitionId());

			if ("static_code_analysis".equalsIgnoreCase(policyDefinitionEntity.getKey())) {
				SonarQubeReport sonarQubeReport = repositoryService.getSonarQubeReport(ciComponentEntity.getId(),
						ciComponentVersionEntity.getName());

				ObjectMapper mapper = new ObjectMapper();
				JsonNode data = mapper.convertValue(sonarQubeReport, JsonNode.class);

				LOGGER.info("static_code_analysis=" + getJsonNodeText(data));

				DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
						policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

				Results result = new Results();
				result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
				result.setDetail(getJsonNodeText(dataResponse.getResult().getDetail()));
				result.setValid(dataResponse.getResult().getValid());

				if (!dataResponse.getResult().getValid()) {
					overallResult = false;
				}

				results.add(result);
				
			} else if ("package_safelist".equalsIgnoreCase(policyDefinitionEntity.getKey())) {
				DependencyGraph dependencyGraph = repositoryService.getDependencyGraph(ciComponentEntity.getId(),
						ciComponentVersionEntity.getName());

				ObjectMapper mapper = new ObjectMapper();
				JsonNode data = mapper.convertValue(dependencyGraph, JsonNode.class);

				LOGGER.info("package_safelist=" + getJsonNodeText(data));

				DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
						policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

				Results result = new Results();
				result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
				result.setDetail(getJsonNodeText(dataResponse.getResult().getDetail()));
				result.setValid(dataResponse.getResult().getValid());

				if (!dataResponse.getResult().getValid()) {
					overallResult = false;
				}

				results.add(result);
				
			} else if ("cve_safelist".equalsIgnoreCase(policyDefinitionEntity.getKey())) {
				ArtifactSummary artifactSummary = repositoryService.getArtifactSummary(ciComponentEntity.getId(),
						ciComponentVersionEntity.getName());

				if (!artifactSummary.getArtifacts().isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode data = mapper.convertValue(artifactSummary.getArtifacts().get(0).getIssues(), JsonNode.class);

					LOGGER.info("cve_safelist=" + getJsonNodeText(data));

					DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
							policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

					Results result = new Results();
					result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
					result.setDetail(getJsonNodeText(dataResponse.getResult().getDetail()));
					result.setValid(dataResponse.getResult().getValid());

					if (!dataResponse.getResult().getValid()) {
						overallResult = false;
					}
					
					results.add(result);					
				}
				else {
					
					Results result = new Results();
					result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
					result.setDetail(null);
					result.setValid(false);

					overallResult = false;
					
					results.add(result);
				}				
			} else if ("security_issue_analysis".equalsIgnoreCase(policyDefinitionEntity.getKey())) {
				ArtifactSummary artifactSummary = repositoryService.getArtifactSummary(ciComponentEntity.getId(),
						ciComponentVersionEntity.getName());

				if (!artifactSummary.getArtifacts().isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode data = mapper.convertValue(artifactSummary.getArtifacts().get(0).getIssues(), JsonNode.class);

					LOGGER.info("security_issue_analysis=" + getJsonNodeText(data));

					DataResponse dataResponse = callOpenPolicyAgentClient(policyDefinitionEntity.getId(),
							policyDefinitionEntity.getKey(), policyConfig.getRules(), data);

					Results result = new Results();
					result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
					result.setDetail(getJsonNodeText(dataResponse.getResult().getDetail()));
					result.setValid(dataResponse.getResult().getValid());

					if (!dataResponse.getResult().getValid()) {
						overallResult = false;
					}
					
					results.add(result);
				}
				else {
					
					Results result = new Results();
					result.setCiPolicyDefinitionId(policyDefinitionEntity.getId());
					result.setDetail(null);
					result.setValid(false);

					overallResult = false;
					
					results.add(result);
				}	
			}
		}

		policiesActivities.setValid(overallResult);
		policiesActivities.setResults(results);

		policiesActivities = ciPolicyActivityService.save(policiesActivities);

		return policiesActivities;
	}

	@Override
	public List<CiPolicyInsights> getInsights(String ciTeamId) {
		Map<String, CiPolicyInsights> insights = new HashMap<>();
		LocalDate date = LocalDate.now().minusMonths(Integer.valueOf(insightsPeriodMonths));

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
			
			Integer failCount = 0;
			for (Results results : activity.getResults()) {
				if (!results.getValid()) {
					failCount++;
				}
			}
			
			CiPolicyActivitiesInsights ciPolicyActivitiesInsights = null;
			
			for (CiPolicyActivitiesInsights activites : ciPolicyInsights.getInsights()) {
				if (activites.getCiPolicyActivityId().equalsIgnoreCase(activity.getId())) {
					ciPolicyActivitiesInsights = activites;
					ciPolicyInsights.getInsights().remove(activites);
					break;
				}
			}
			
			if (ciPolicyActivitiesInsights == null) {
				ciPolicyActivitiesInsights = new CiPolicyActivitiesInsights();
				ciPolicyActivitiesInsights.setCiPolicyActivityId(activity.getId());
				ciPolicyActivitiesInsights.setCiPolicyActivityCreatedDate(activity.getCreatedDate());
				ciPolicyActivitiesInsights.setViolations(failCount);
			}
			else {
				ciPolicyActivitiesInsights.setViolations(ciPolicyActivitiesInsights.getViolations() + failCount);
			}				
			
			ciPolicyInsights.getInsights().add(ciPolicyActivitiesInsights);			
			insights.put(ciPolicyId, ciPolicyInsights);
		}
		
		return new ArrayList<CiPolicyInsights>(insights.values());
	}
	
	@Override
	public List<CiPolicyViolations> getViolations(String ciTeamId) {

		List<CiStageEntity> stagesWithGates = new ArrayList<CiStageEntity>();		
		List<CiPipelineEntity> pipelines = ciPipelineService.findByCiTeamId(ciTeamId);
		for (CiPipelineEntity pipeline : pipelines) {
			List<CiStageEntity> stages = ciStagesService.findByPipelineId(pipeline.getId());
			for (CiStageEntity stage : stages) {
				if (stage.getGates() != null && stage.getGates().getEnabled()) {
					stagesWithGates.add(stage);
				}
			}
		}
		
		LOGGER.info("stagesWithGates.count=" + stagesWithGates.size());
		
		Map<String, CiPolicyViolations> violationsMap = new HashMap<String, CiPolicyViolations>();
		
		List<CiComponentEntity> components = ciComponentService.findByCiTeamId(ciTeamId);
		for (CiComponentEntity component : components) {
			if (component.getIsActive()) {
				
				LOGGER.info("component.name=" + component.getName());
				
				for (CiStageEntity stage : stagesWithGates) {
					CiComponentActivityEntity componentActivity = ciComponentActivityService.findTopByCiComponentIdAndTypeAndCiStageIdOrderByCreationDateDesc(component.getId(), CiComponentActivityType.BUILD, stage.getId());
					if (componentActivity != null) {	
						
						LOGGER.info("componentActivity.id=" + componentActivity.getId());
						
						List<CiPolicyActivityEntity> policyActivities = ciPolicyActivityService.findByCiComponentActivityId(componentActivity.getId());
						
						LOGGER.info("policyActivities.size=" + policyActivities.size());
						
						for (CiPolicyActivityEntity policyActivity : policyActivities) {
							if (!policyActivity.getValid()) {																
								
								CiPolicyEntity policy = ciPolicyService.findById(policyActivity.getCiPolicyId());
								
								LOGGER.info("policy.name=" + policy.getName());
								
								CiComponentVersionEntity componentVersion = ciComponentVersionService.findVersionWithId(componentActivity.getCiComponentVersionId());
								
								LOGGER.info("componentVersion.name=" + componentVersion.getName());
																
								StringBuffer key = new StringBuffer();
								key.append(policy.getId()).append(component.getId()).append(componentVersion.getId()).append(stage.getId());
								
								LOGGER.info("key=" + key.toString());
								
								CiPolicyViolations violation = violationsMap.get(key.toString());								
								if (violation == null) {
									violation = new CiPolicyViolations();
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
								}
								else if (policyActivity.getCreatedDate().after(violation.getCiPolicyActivityCreatedDate())) {
									violation.setViolations(0);				
									violation.setCiPolicyActivityCreatedDate(policyActivity.getCreatedDate());
								}
								
								Integer violationsTotal = 0;								
								for (Results result : policyActivity.getResults()) {
									if (!result.getValid()) {										
										violationsTotal++;
									}
								}							
								violation.setViolations(violation.getViolations() + violationsTotal);
								
								violationsMap.put(key.toString(), violation);
							}
						}					
					}
				}						
			}
		}		
		
		return new ArrayList<CiPolicyViolations>(violationsMap.values());
	}

	private DataResponse callOpenPolicyAgentClient(String policyDefinitionId, String policyDefinitionKey,
			List<Map<String, String>> rules, JsonNode data) {

		DataRequestPolicy dataRequestPolicy = new DataRequestPolicy();
		dataRequestPolicy.setId(policyDefinitionId);
		dataRequestPolicy.setKey(policyDefinitionKey);
		dataRequestPolicy.setRules(rules);

		DataRequestInput dataRequestInput = new DataRequestInput();
		dataRequestInput.setPolicy(dataRequestPolicy);
		dataRequestInput.setData(data);

		DataRequest dataRequest = new DataRequest();
		dataRequest.setInput(dataRequestInput);

		return openPolicyAgentClient.validateData(dataRequest);
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
		List<String> stagesForPolicy = new ArrayList<String>();
		
		List<CiPipelineEntity> pipelines = ciPipelineService.findByCiTeamId(ciTeamId);
		for (CiPipelineEntity pipeline : pipelines) {
			List<CiStageEntity> stages = ciStagesService.findByPipelineId(pipeline.getId());
			for (CiStageEntity stage : stages) {
				if (stage.getGates() != null && stage.getGates().getEnabled() && stage.getGates().getPolicies().contains(ciPolicyId)) {
					stagesForPolicy.add(stage.getName());
				}
			}
		}
		
		return stagesForPolicy;
	}
}
