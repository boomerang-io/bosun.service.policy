package net.boomerangplatform.service;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import net.boomerangplatform.Application;
import net.boomerangplatform.MongoConfig;
import net.boomerangplatform.model.PolicyTeam;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
public class TeamServiceTest extends BosunTests {

  @Autowired
  private PolicyTeamService teamService;

  @Test
  public void testGetTeams() {
    List<PolicyTeam> teams = teamService.getAllTeams();

    Assert.assertEquals(0, teams.size());
  }
}
