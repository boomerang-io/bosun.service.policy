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
import net.boomerangplatform.mongo.model.Audit;
import net.boomerangplatform.team.model.CiTeam;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
public class TeamServiceTest extends AbstractBoomerangTest {

  @Autowired
  private TeamService teamService;

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
    List<CiTeam> teams = teamService.getTeams();

    Assert.assertEquals(1, teams.size());

    CiTeam team = teams.get(0);
    Assert.assertEquals(1, team.getAudits().size());
    Audit audit = team.getAudits().get(0);
    Assert.assertEquals("Team created", audit.getNote());
    Assert.assertNull(audit.getStatus());
    Assert.assertNull(audit.getAuditerId());

    Assert.assertEquals("Test Team", team.getBoomerangTeamName());
    Assert.assertEquals("Test Team", team.getBoomerangTeamShortname());
    Assert.assertEquals("Test Team", team.getName());
    Assert.assertTrue(team.getIsActive());
    Assert.assertEquals("Test Team", team.getName());
    Assert.assertEquals("5cedb53261a23a0001e4c1b6", team.getHigherLevelGroupId());
    Assert.assertEquals("16b008bf-4df7-1bac-ff6e-c214c77ad84c", team.getUcdApplicationId());
  }
}
