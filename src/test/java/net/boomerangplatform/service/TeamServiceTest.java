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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.MongoConfig;
import net.boomerangplatform.model.PolicyTeam;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
public class TeamServiceTest extends AbstractBoomerangTest {

  @Autowired
  private PolicyTeamService teamService;

  @Override
  protected String[] getCollections() {
    return new String[] {"ci_teams", "core_groups_higher_level"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_teams",
        Arrays.asList("db/ci_teams/CiTeamEntity.json", "db/ci_teams/CiTeamEntity2.json"));
    data.put("core_groups_higher_level",
        Arrays.asList("db/core_groups_higher_level/BoomerangTeamEntity.json",
            "db/core_groups_higher_level/BoomerangTeamEntity2.json"));

    return data;
  }

  @Test
  public void testGetTeams() {
    List<PolicyTeam> teams = teamService.getAllTeams();

    Assert.assertEquals(0, teams.size());
  }
}
