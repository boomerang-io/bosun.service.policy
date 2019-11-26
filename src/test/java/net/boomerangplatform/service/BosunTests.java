package net.boomerangplatform.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.boomerangplatform.AbstractBoomerangTest;

public class BosunTests extends AbstractBoomerangTest {

  @Override
  protected String[] getCollections() {
    return new String[] {"bosun_activities", "bosun_definitions", "bosun_policies", "bosun_teams",
        "bosun_templates"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("bosun_activities", Arrays.asList("db/bosun_activities/BosunActivity1.json",
        "db/bosun_activities/BosunActivity2.json", "db/bosun_activities/BosunActivity3.json",
        "db/bosun_activities/BosunActivity4.json", "db/bosun_activities/BosunActivity5.json",
        "db/bosun_activities/BosunActivity6.json", "db/bosun_activities/BosunActivity7.json",
        "db/bosun_activities/BosunActivity8.json", "db/bosun_activities/BosunActivity9.json",
        "db/bosun_activities/BosunActivity10.json", "db/bosun_activities/BosunActivity11.json",
        "db/bosun_activities/BosunActivity12.json", "db/bosun_activities/BosunActivity13.json",
        "db/bosun_activities/BosunActivity14.json", "db/bosun_activities/BosunActivity15.json"));

    data.put("bosun_definitions",
        Arrays.asList("db/bosun_definitions/BosunDefinitionEntity1.json",
            "db/bosun_definitions/BosunDefinitionEntity2.json",
            "db/bosun_definitions/BosunDefinitionEntity3.json",
            "db/bosun_definitions/BosunDefinitionEntity4.json",
            "db/bosun_definitions/BosunDefinitionEntity5.json"));


    data.put("bosun_policies", Arrays.asList("db/bosun_policies/BosunPolicyEntity1.json",
        "db/bosun_policies/BosunPolicyEntity2.json", "db/bosun_policies/BosunPolicyEntity3.json",
        "db/bosun_policies/BosunPolicyEntity4.json", "db/bosun_policies/BosunPolicyEntity5.json",
        "db/bosun_policies/BosunPolicyEntity6.json", "db/bosun_policies/BosunPolicyEntity7.json",
        "db/bosun_policies/BosunPolicyEntity8.json", "db/bosun_policies/BosunPolicyEntity9.json",
        "db/bosun_policies/BosunPolicyEntity10.json",
        "db/bosun_policies/BosunPolicyEntity11.json"));

    data.put("bosun_teams", Arrays.asList("db/bosun_teams/BosunTeamEntity1.json",
        "db/bosun_teams/BosunTeamEntity2.json"));

    data.put("bosun_templates",
        Arrays.asList("db/bosun_templates/BosunTemplateEntity1.json",
            "db/bosun_templates/BosunTemplateEntity2.json",
            "db/bosun_templates/BosunTemplateEntity3.json",
            "db/bosun_templates/BosunTemplateEntity4.json",
            "db/bosun_templates/BosunTemplateEntity5.json",
            "db/bosun_templates/BosunTemplateEntity6.json"));

    return data;
  }

}
