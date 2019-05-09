package net.boomerangplatform.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.boomerangplatform.citadel.model.CiPolicy;
import net.boomerangplatform.citadel.model.CiPolicyDefinition;
import net.boomerangplatform.service.CitadelService;

@RestController
@RequestMapping("/citadel")
public class CitadelController {

  @Autowired
  private CitadelService citadelService;

  @GetMapping(value = "/teams")
  public ResponseEntity<?> getAllTeams() {
    return ResponseEntity.ok().build(); // TODO same with ci service getTeams
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

  @PostMapping(value = "/policies/policies")
  public ResponseEntity<CiPolicy> addPolicy(
      @RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(citadelService.addPolicy(policy));
  }

  @PatchMapping(value = "/policies/policies")
  public ResponseEntity<CiPolicy> updatePolicy(
      @RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(citadelService.updatePolicy(policy));
  }

  @GetMapping(value = "/policies/policies/violations")
  public ResponseEntity<?> getViolations(
      @RequestParam(value = "teamId", required = true) String teamId) { // TODO:
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies/policies/insights")
  public ResponseEntity<?> getInsights(
      @RequestParam(value = "teamId", required = true) String teamId) { // TODO:
    return ResponseEntity.ok().build();
  }
}
