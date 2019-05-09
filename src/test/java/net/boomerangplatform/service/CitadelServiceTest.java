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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;
import net.boomerangplatform.mongo.model.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public class CitadelServiceTest extends AbstractBoomerangTest {

  @Autowired
  private CitadelService citadelService;

  @Override
  protected String[] getCollections() {
    return new String[] {"ci_policies", "ci_policies_definitions"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_policies", Arrays.asList("db/ci_policies/CiPolicyEntity.json"));
    data.put("ci_policies_definitions",
        Arrays.asList("db/ci_policies_definitions/CiPolicyDefinitionEntity.json"));

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
    Assert.assertEquals(2, definition.getRules().size());
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
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", policy.getCiPolicyDefinitionId());

    Assert.assertEquals(teamId, policy.getTeamId());
    Assert.assertEquals(2, policy.getRules().size());
    Property rule = policy.getRules().get(0);

    Assert.assertEquals("lines", rule.getKey());
    Assert.assertEquals("88", rule.getValue());
  }


  @Test
  public void testAddPolicy() throws IOException {
    CiPolicy policy = new ObjectMapper().readValue(loadResourceAsString("addCiPolicyEntity.json"),
        CiPolicy.class);
    CiPolicy policyReturn = citadelService.addPolicy(policy);

    Assert.assertEquals("Code High Validation", policyReturn.getName());

    String definitionId = policyReturn.getCiPolicyDefinitionId();
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definitionId);


    List<CiPolicy> policies = citadelService.getPoliciesByTeamId("9999");
    Assert.assertEquals(2, policies.size());

    CiPolicy policyFound = policies.get(0);
    Assert.assertEquals(definitionId, policyFound.getCiPolicyDefinition().getId());
  }


  @Test
  public void testUpdatePolicy() throws IOException {
    CiPolicy policy = new ObjectMapper()
        .readValue(loadResourceAsString("updateCiPolicyEntity.json"), CiPolicy.class);
    CiPolicy policyReturn = citadelService.updatePolicy(policy);

    System.out.println(parseToJson(policyReturn));

    Assert.assertEquals("Code Low Validation", policyReturn.getName());

    String definitionId = policyReturn.getCiPolicyDefinitionId();
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definitionId);


    List<CiPolicy> policies = citadelService.getPoliciesByTeamId("9999");
    Assert.assertEquals(1, policies.size());

    CiPolicy policyFound = policies.get(0);
    Assert.assertEquals(definitionId, policyFound.getCiPolicyDefinition().getId());
    Assert.assertEquals("Code Low Validation", policyFound.getName());
  }
}
