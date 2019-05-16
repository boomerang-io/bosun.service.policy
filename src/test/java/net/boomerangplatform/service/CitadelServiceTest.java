package net.boomerangplatform.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyActivitiesInsights;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.mongo.model.CiPolicyConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "local")
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public class CitadelServiceTest extends AbstractBoomerangTest {

  @Autowired
  private CitadelService citadelService;

  @Override
  protected String[] getCollections() {
    return new String[] {"ci_policies", "ci_policies_definitions", "ci_policies_activities"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_policies", Arrays.asList("db/ci_policies/CiPolicyEntity.json"));
    data.put("ci_policies_definitions",
        Arrays.asList("db/ci_policies_definitions/CiPolicyDefinitionEntity.json"));
    data.put("ci_policies_activities",
        Arrays.asList("db/ci_policies_activities/CiPolicyActivityEntity.json",
            "db/ci_policies_activities/CiPolicyActivityEntity2.json",
            "db/ci_policies_activities/CiPolicyActivityEntity3.json"));


    return data;
  }

  @Test
  public void testGetAllDefinitions() {
    List<CiPolicyDefinition> definitions = citadelService.getAllDefinitions();

    Assert.assertEquals(1, definitions.size());
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
    String teamId = "9999";

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


    List<CiPolicy> policies = citadelService.getPoliciesByTeamId("9999");
    Assert.assertEquals(2, policies.size());

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
		Assert.assertEquals(Integer.valueOf(1), failCount);
	}
}
