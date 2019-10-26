package net.boomerangplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.model.PolicyTeam;
import net.boomerangplatform.service.PolicyTeamService;

@RestController
@RequestMapping("/bosun/teams")
public class BosunTeamController {

  @Autowired
  private PolicyTeamService teamService;

  @GetMapping(value = "")
  public List<PolicyTeam> getAllTeams() {
    return teamService.getAllTeams();
  }
}
