package net.boomerangplatform.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.mongo.entity.CiPolicyActivityEntity;
import net.boomerangplatform.service.CitadelService;
import net.boomerangplatform.service.TeamService;
import net.boomerangplatform.team.model.CiTeam;

@RestController
@RequestMapping("/citadel")
public class CitadelController {

  @Autowired
  private CitadelService citadelService;

  @Autowired
  private TeamService teamService;

  @GetMapping(value = "/teams")
  public ResponseEntity<List<CiTeam>> getAllTeams() {
    return ResponseEntity.ok().body(teamService.getTeams());
  }

  @GetMapping(value = "/policies/definitions")
  public ResponseEntity<List<CiPolicyDefinition>> getAllDefinitions() {
    return ResponseEntity.ok().body(citadelService.getAllDefinitions());
  }

  @GetMapping(value = "/policies/operators")
  public ResponseEntity<Map<String, String>> getAllOperators() {
    return ResponseEntity.ok().body(citadelService.getAllOperators());
  }

  @GetMapping(value = "/policies")
  public ResponseEntity<List<CiPolicy>> getPolicies(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().body(citadelService.getPoliciesByTeamId(teamId));
  }

  @GetMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<CiPolicy> getPolicy(@PathVariable String ciPolicyId) {
    return ResponseEntity.ok().body(citadelService.getPolicyById(ciPolicyId));
  }

  @PostMapping(value = "/policies")
  public ResponseEntity<CiPolicy> addPolicy(@RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(citadelService.addPolicy(policy));
  }

  @PatchMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<CiPolicy> updatePolicy(@PathVariable String ciPolicyId,
      @RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(citadelService.updatePolicy(policy));
  }

  @GetMapping(value = "/policies/violations")
  public ResponseEntity<Map<CiPolicy, Integer>> getViolations(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().body(citadelService.getInsights(teamId));
  }

  @GetMapping(value = "/policies/insights")
  public ResponseEntity<?> getInsights(
      @RequestParam(value = "teamId", required = true) String teamId) { // TODO:
    return ResponseEntity.ok().build();
  }


  @GetMapping(value = "/policies/validate")
  public ResponseEntity<CiPolicyActivityEntity> validatePolicy(
      @RequestParam(value = "ciComponentId", required = true) String ciComponentId,
      @RequestParam(value = "ciVersionId", required = true) String ciVersionId,
      @RequestParam(value = "ciPolicyId", required = true) String ciPolicyId) {
    return ResponseEntity.ok()
        .body(citadelService.validatePolicy(ciComponentId, ciVersionId, ciPolicyId));
  }
}
