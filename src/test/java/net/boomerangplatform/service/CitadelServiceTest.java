package net.boomerangplatform.service;

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
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public class CitadelServiceTest extends AbstractBoomerangTest{
  
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
    data.put("ci_policies_definitions", Arrays.asList("db/ci_policies_definitions/CiPolicyDefinitionEntity.json"));

    return data;

  }
  
  @Test
  public void testGetAllDefinitions() {
    List<CiPolicyDefinition> definitions = citadelService.getAllDefinitions();
    
    Assert.assertEquals(1, definitions.size());
    CiPolicyDefinition definition = definitions.get(0);
    Assert.assertEquals("5cd328ae1e9bbbb710590d9d", definition.getId());
    Assert.assertEquals("Static Code Analysis", definition.getName());
    Assert.assertEquals("The following policy metrics are retrieved from SonarQube", definition.getDescription());
    Assert.assertEquals("static_code_analysis", definition.getKey());
    Assert.assertEquals(0, definition.getOrder().intValue());
    Assert.assertEquals(2, definition.getRules().size());
  }
}
