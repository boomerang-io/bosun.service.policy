package net.boomerangplatform.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import net.boomerangplatform.Application;
import net.boomerangplatform.model.PolicyTeam;

@ActiveProfiles(profiles = "test")
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public class TeamServiceTest extends BosunTests {

  @Autowired
  private PolicyTeamService teamService;

  @Test
  public void testGetTeams() {
    List<PolicyTeam> teams = teamService.getAllTeams();

    Assertions.assertEquals(2, teams.size());
  }
}
