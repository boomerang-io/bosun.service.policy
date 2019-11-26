package net.boomerangplatform.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.boomerangplatform.AbstractBoomerangTest;

public abstract class BosunTest extends AbstractBoomerangTest {

  @Override
  protected String[] getCollections() {
    return new String[] {"ci_policies", "ci_policies_definitions", "ci_policies_activities",
        "ci_components", "ci_components_activities", "ci_components_versions", "ci_pipelines",
        "ci_stages", "ci_teams", "core_groups_higher_level"};
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
        Arrays.asList("db/ci_policies_activities/PolicyActivityEntity.json",
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
    data.put("ci_teams",
        Arrays.asList("db/ci_teams/CiTeamEntity.json", "db/ci_teams/CiTeamEntity2.json"));
    data.put("core_groups_higher_level",
        Arrays.asList("db/core_groups_higher_level/BoomerangTeamEntity.json",
            "db/core_groups_higher_level/BoomerangTeamEntity2.json"));


    return data;
  }

}
