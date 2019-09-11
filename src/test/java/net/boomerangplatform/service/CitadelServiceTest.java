package net.boomerangplatform.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.MongoConfig;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyActivitiesInsights;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.mongo.entity.CiPolicyActivityEntity;
import net.boomerangplatform.mongo.model.CiPolicyConfig;
import net.boomerangplatform.repository.model.Artifact;
import net.boomerangplatform.repository.model.ArtifactPackage;
import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.Cfe;
import net.boomerangplatform.repository.model.Component;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.General;
import net.boomerangplatform.repository.model.Issue;
import net.boomerangplatform.repository.model.Issues;
import net.boomerangplatform.repository.model.License;
import net.boomerangplatform.repository.model.Measures;
import net.boomerangplatform.repository.model.SonarQubeReport;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
public class CitadelServiceTest extends AbstractBoomerangTest {

  private final static LocalDate LOCAL_DATE = LocalDate.of(2019, 05, 15);

  @Autowired
  private CitadelService citadelService;

  @Autowired
  @Qualifier("internalRestTemplate")
  @Resource(name = "internalRestTemplate")
  RestTemplate restTemplate;

  private MockRestServiceServer server;

  @MockBean
  private Clock clock;

  private Clock fixedClock;

  @Override
  public void setUp() {
    super.setUp();
    fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault());
    when(clock.instant()).thenReturn(fixedClock.instant());
    when(clock.getZone()).thenReturn(fixedClock.getZone());
  }


  @Override
  protected String[] getCollections() {
    return new String[] {"ci_policies", "ci_policies_definitions", "ci_policies_activities",
        "ci_components", "ci_components_activities", "ci_components_versions", "ci_pipelines",
        "ci_stages"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_policies", Arrays.asList("db/ci_policies/CiPolicyEntity1.json",
        "db/ci_policies/CiPolicyEntity2.json", "db/ci_policies/CiPolicyEntity3.json"));
    data.put("ci_policies_definitions",
        Arrays.asList("db/ci_policies_definitions/CiPolicyDefinitionEntity1.json",
            "db/ci_policies_definitions/CiPolicyDefinitionEntity2.json",
            "db/ci_policies_definitions/CiPolicyDefinitionEntity3.json"));
    data.put("ci_policies_activities",
        Arrays.asList("db/ci_policies_activities/CiPolicyActivityEntity.json",
            "db/ci_policies_activities/CiPolicyActivityEntity2.json",
            "db/ci_policies_activities/CiPolicyActivityEntity3.json",
            "db/ci_policies_activities/CiPolicyActivityEntity4.json",
            "db/ci_policies_activities/CiPolicyActivityEntity5.json",
            "db/ci_policies_activities/CiPolicyActivityEntity6.json"));
    data.put("ci_components_activities",
        Arrays.asList("db/ci_components_activities/CiComponentActivityEntity.json"));
    data.put("ci_components_versions",
        Arrays.asList("db/ci_components_versions/CiComponentVersionEntity.json"));
    data.put("ci_components", Arrays.asList("db/ci_components/CiComponentEntity.json"));
    data.put("ci_pipelines", Arrays.asList("db/ci_pipelines/CiPipelineEntity.json"));
    data.put("ci_stages", Arrays.asList("db/ci_stages/CiStageEntity.json"));


    return data;
  }

  @Test
  public void testGetAllDefinitions() {
    List<CiPolicyDefinition> definitions = citadelService.getAllDefinitions();

    Assert.assertEquals(3, definitions.size());
    CiPolicyDefinition definition = definitions.get(0);
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definition.getId());
    Assert.assertEquals("Static Code Analysis", definition.getName());
    Assert.assertEquals("The following policy metrics are retrieved from SonarQube",
        definition.getDescription());
    Assert.assertEquals("static_code_analysis", definition.getKey());
    Assert.assertEquals(0, definition.getOrder().intValue());
    Assert.assertEquals(3, definition.getConfig().size());
  }

  @Test
  public void testGetAllOperators() throws JsonProcessingException {
    Map<String, String> operators = citadelService.getAllOperators();

    Assert.assertEquals(5, operators.size());
    Assert.assertEquals("Equals", operators.get("EQUALS"));
  }

  @Test
  public void testGetPoliciesByTeamId() throws JsonProcessingException {
    String teamId = "5cedb53fdd1be20001f3d8c2";

    List<CiPolicy> policies = citadelService.getPoliciesByTeamId(teamId);

    Assert.assertEquals(1, policies.size());

    CiPolicy policy = policies.get(0);

    Assert.assertEquals("Code Medium Validation", policy.getName());
    Assert.assertEquals("5c5b5a0b352b1b614143b7c3", policy.getId());
    Assert.assertEquals(teamId, policy.getTeamId());

    Assert.assertEquals(1, policy.getDefinitions().size());

    CiPolicyConfig definition = policy.getDefinitions().get(0);
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definition.getCiPolicyDefinitionId());

    Assert.assertEquals(2, definition.getRules().size());
    Map<String, String> rule = definition.getRules().get(0);

    Assert.assertEquals("lines", rule.get("metric"));
    Assert.assertEquals("88", rule.get("value"));
  }


  @Test
  public void testAddPolicy() throws IOException {
    CiPolicy policy = new ObjectMapper().readValue(loadResourceAsString("addCiPolicyEntity.json"),
        CiPolicy.class);
    CiPolicy policyReturn = citadelService.addPolicy(policy);

    System.out.println(parseToJson(policyReturn));

    Assert.assertEquals("Code High Validation", policyReturn.getName());
    Assert.assertEquals(1, policyReturn.getDefinitions().size());

    CiPolicyConfig definition = policyReturn.getDefinitions().get(0);


    String definitionId = definition.getCiPolicyDefinitionId();
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definitionId);


    List<CiPolicy> policies = citadelService.getPoliciesByTeamId("5cedb53fdd1be20001f3d8c2");
    Assert.assertEquals(1, policies.size());

    CiPolicy policyFound = policies.get(0);
    Assert.assertEquals(definitionId,
        policyFound.getDefinitions().get(0).getCiPolicyDefinitionId());
  }


  @Test
  public void testUpdatePolicy() throws IOException {
    CiPolicy policy = new ObjectMapper()
        .readValue(loadResourceAsString("updateCiPolicyEntity.json"), CiPolicy.class);
    CiPolicy policyReturn = citadelService.updatePolicy(policy);

    System.out.println(parseToJson(policyReturn));

    Assert.assertEquals("Code Low Validation", policyReturn.getName());

    CiPolicyConfig ciPolicyConfig = policyReturn.getDefinitions().get(0);
    String definitionId = ciPolicyConfig.getCiPolicyDefinitionId();
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definitionId);


    List<CiPolicy> policies = citadelService.getPoliciesByTeamId("9999");
    Assert.assertEquals(1, policies.size());

    CiPolicy policyFound = policies.get(0);
    Assert.assertEquals("Code Low Validation", policyFound.getName());
  }

  @Test
  public void testGetInsights() throws IOException {
    List<CiPolicyInsights> insights = citadelService.getInsights("9999");

    Assert.assertEquals(1, insights.size());
    CiPolicyInsights entry = insights.get(0);
    CiPolicy policy = citadelService.getPolicyById(entry.getCiPolicyId());
    Assert.assertEquals("Code Medium Validation", policy.getName());
    Assert.assertEquals("5c5b5a0b352b1b614143b7c3", policy.getId());
    Integer failCount = 0;
    for (CiPolicyActivitiesInsights ciPolicyActivitiesInsights : entry.getInsights()) {
      failCount += ciPolicyActivitiesInsights.getViolations();
    }
    Assert.assertEquals(Integer.valueOf(3), failCount);
  }

  @Test
  public void testValidatePolicyWithStaticCodeAnalyse() throws JsonProcessingException {

    this.server = MockRestServiceServer.createServer(restTemplate);

    String sonarQubeURL =
        "http://localhost:8080/repository/sonarqube/report?ciComponentId=5cedbec5dd1be20001f3d942&version=nextgen-2";

    this.server.expect(requestTo(sonarQubeURL))
        .andRespond(withSuccess(parseToJson(getSonarQubeReport()), MediaType.APPLICATION_JSON));

    String opaURL = "http://localhost:8181/v1/data/citadel/static_code_analysis";

    this.server.expect(requestTo(opaURL)).andRespond(
        withSuccess(loadResourceAsString("dataResponse.json"), MediaType.APPLICATION_JSON));

    String componentActivityId = "5cee1d76dd1be20001f3d9c5";
    String ciPolicyId = "5c5b5a0b352b1b614143b7c3";

    CiPolicyActivityEntity savedEntity =
        citadelService.validatePolicy(componentActivityId, ciPolicyId);

    Assert.assertEquals("5c5b5a0b352b1b614143b7c3", savedEntity.getCiPolicyId());
    Assert.assertEquals(componentActivityId, savedEntity.getCiComponentActivityId());
    Assert.assertEquals("5cedb53fdd1be20001f3d8c2", savedEntity.getCiTeamId());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getValid());
    Assert.assertEquals(1, savedEntity.getResults().size());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getResults().get(0).getValid());
//    Assert.assertEquals("[]", savedEntity.getResults().get(0).getViolations());
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d",
        savedEntity.getResults().get(0).getCiPolicyDefinitionId());

    server.verify();

  }

  @Test
  public void testValidatePolicyWithCveSafelist() throws JsonProcessingException {

    this.server = MockRestServiceServer.createServer(restTemplate);

    String sonarQubeURL =
        "http://localhost:8080/repository/xray/artifact/summary?ciComponentId=5cedbec5dd1be20001f3d942&version=nextgen-2";

    this.server.expect(requestTo(sonarQubeURL))
        .andRespond(withSuccess(parseToJson(getArtifactSummary()), MediaType.APPLICATION_JSON));

    String opaURL = "http://localhost:8181/v1/data/citadel/cve_safelist";

    this.server.expect(requestTo(opaURL)).andRespond(
        withSuccess(loadResourceAsString("dataResponse.json"), MediaType.APPLICATION_JSON));

    String componentActivityId = "5cee1d76dd1be20001f3d9c5";
    String ciPolicyId = "5cf151691417760001c0a679";

    CiPolicyActivityEntity savedEntity =
        citadelService.validatePolicy(componentActivityId, ciPolicyId);

    Assert.assertEquals("5cf151691417760001c0a679", savedEntity.getCiPolicyId());
    Assert.assertEquals(componentActivityId, savedEntity.getCiComponentActivityId());
    Assert.assertEquals("5cedb53fdd1be20001f3d8c2", savedEntity.getCiTeamId());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getValid());
    Assert.assertEquals(1, savedEntity.getResults().size());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getResults().get(0).getValid());
//    Assert.assertEquals("[]", savedEntity.getResults().get(0).getViolations());
    Assert.assertEquals("5cdd8425f6ea74a9bbaf2fe6",
        savedEntity.getResults().get(0).getCiPolicyDefinitionId());

    server.verify();

  }

  @Test
  public void testValidatePolicyWithPackageSafelist() throws JsonProcessingException {

    this.server = MockRestServiceServer.createServer(restTemplate);

    String sonarQubeURL =
        "http://localhost:8080/repository/xray/artifact/dependencygraph?ciComponentId=5cedbec5dd1be20001f3d942&version=nextgen-2";

    this.server.expect(requestTo(sonarQubeURL))
        .andRespond(withSuccess(parseToJson(getDependencyGraph()), MediaType.APPLICATION_JSON));

    String opaURL = "http://localhost:8181/v1/data/citadel/package_safelist";

    this.server.expect(requestTo(opaURL)).andRespond(
        withSuccess(loadResourceAsString("dataResponse.json"), MediaType.APPLICATION_JSON));

    String componentActivityId = "5cee1d76dd1be20001f3d9c5";
    String ciPolicyId = "5cf151691417760001c0a675";

    CiPolicyActivityEntity savedEntity =
        citadelService.validatePolicy(componentActivityId, ciPolicyId);

    Assert.assertEquals("5cf151691417760001c0a675", savedEntity.getCiPolicyId());
    Assert.assertEquals(componentActivityId, savedEntity.getCiComponentActivityId());
    Assert.assertEquals("5cedb53fdd1be20001f3d8c2", savedEntity.getCiTeamId());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getValid());
    Assert.assertEquals(1, savedEntity.getResults().size());
    Assert.assertEquals(Boolean.FALSE, savedEntity.getResults().get(0).getValid());
//    Assert.assertEquals("[]", savedEntity.getResults().get(0).getViolations());
    Assert.assertEquals("5cd498f3f6ea74a9bb6ad0f3",
        savedEntity.getResults().get(0).getCiPolicyDefinitionId());

    server.verify();

  }

  @Test
  public void testGetViolations() throws JsonProcessingException {
    String teamId = "5cedb53fdd1be20001f3d8c2";
    
    List<CiPolicyViolations> violations = citadelService.getViolations(teamId);
    Assert.assertEquals(1, violations.size());
    CiPolicyViolations violation = violations.get(0);
    Assert.assertEquals("5cedbec5dd1be20001f3d942", violation.getCiComponentId());
    Assert.assertEquals("next-gen-docker", violation.getCiComponentName());
    Assert.assertEquals("5cee1d76dd1be20001f3d9c4", violation.getCiComponentVersionId());
    Assert.assertEquals("nextgen-2", violation.getCiComponentVersionName());
    Assert.assertEquals("5cf151691417760001c0a679", violation.getCiPolicyId());
    Assert.assertEquals("Glens Zero Defns Test", violation.getCiPolicyName());
    Assert.assertEquals("5cedf589dd1be20001f3d994", violation.getCiStageId());
    Assert.assertEquals("dev", violation.getCiStageName());
    Assert.assertEquals(2, violation.getNbrViolations().intValue());
  }

  private SonarQubeReport getSonarQubeReport() {
    SonarQubeReport report = new SonarQubeReport();

    Issues issues = new Issues();
    issues.setBlocker(1);
    issues.setCritical(2);
    issues.setFilesAnalyzed(99);
    issues.setInfo(1);
    issues.setMajor(0);
    issues.setMinor(15);
    issues.setTotal(19);

    Measures measures = new Measures();
    measures.setComplexity(9);
    measures.setNcloc(1);
    measures.setViolations(3);

    report.setIssues(issues);
    report.setMeasures(measures);

    return report;
  }

  private ArtifactSummary getArtifactSummary() {
    ArtifactSummary artifactSummary = new ArtifactSummary();

    Artifact artifact = new Artifact();
    Issue issue = new Issue();
    issue.setCreated("admin");
    issue.setSeverity("severity");
    issue.setDescription("description");
    issue.setIssueType("BUG");
    issue.setSummary("summary");
    issue.setProvider("provider");
    Cfe cfe = new Cfe();
    cfe.setCve("cve");
    cfe.setCvssV2("cvssV2");
    cfe.setCvssV3("cvssV3");
    cfe.setCwe(Arrays.asList("cwe"));
    issue.setCves(Arrays.asList(cfe));
    issue.setImpactPath(Arrays.asList("impactPaths"));

    General general = new General();
    general.setComponentId("componentId");
    general.setName("name");
    general.setPath("path");
    general.setPkgType("pkgType");
    general.setSha256("sha256");

    artifact.setIssues(Arrays.asList(issue));
    artifact.setGeneral(general);
    License license = new License();
    license.setComponents(Arrays.asList("componentId"));
    license.setFullName("fullName");
    license.setMoreInfoUrl(Arrays.asList("moreInfoUrl"));
    license.setName("name");
    artifact.setLicenses(Arrays.asList(license));
    artifactSummary.setArtifacts(Arrays.asList(artifact));

    return artifactSummary;
  }

  private DependencyGraph getDependencyGraph() {
    DependencyGraph dependencyGraph = new DependencyGraph();

    ArtifactPackage artifact = new ArtifactPackage();
    artifact.setComponentId("componentId");
    artifact.setName("name");
    artifact.setPath("path");
    artifact.setPkgType("pkgType");
    artifact.setSha256("sha256");
    dependencyGraph.setArtifact(artifact);

    Component component = new Component();
    component.setComponentId("componentId");
    component.setComponentName("componentName");
    component.setCreated("admin");
    component.setPackageType("packageType");

    dependencyGraph.setComponents(Arrays.asList(component));

    return dependencyGraph;
  }

}
