package net.boomerangplatform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.entity.PolicyActivityEntity;
import net.boomerangplatform.model.Policy;
import net.boomerangplatform.model.PolicyDefinition;
import net.boomerangplatform.model.PolicyInsights;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.PolicyValidation;
import net.boomerangplatform.model.PolicyViolations;
import net.boomerangplatform.service.BosunService;
import net.boomerangplatform.service.TeamService;
import net.boomerangplatform.team.model.CiTeam;

@RestController
@RequestMapping("/bosun")
public class BosunController {

  @Autowired
  private BosunService bosunService;

  @Autowired
  private TeamService teamService;

  @GetMapping(value = "/teams")
  public ResponseEntity<List<CiTeam>> getAllTeams() {
    return ResponseEntity.ok().body(teamService.getTeams());
  }

  @GetMapping(value = "/definitions")
  public ResponseEntity<List<PolicyDefinition>> getAllDefinitions() {
    return ResponseEntity.ok().body(bosunService.getAllDefinitions());
  }

  @GetMapping(value = "/policies/operators")
  public ResponseEntity<Map<String, String>> getAllOperators() {
    return ResponseEntity.ok().body(bosunService.getAllOperators());
  }

  @GetMapping(value = "/policies")
  public ResponseEntity<List<Policy>> getPolicies(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getPoliciesByTeamId(ciTeamId));
  }

  @GetMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<Policy> getPolicy(@PathVariable String ciPolicyId) {
    return ResponseEntity.ok().body(bosunService.getPolicyById(ciPolicyId));
  }

  @PostMapping(value = "/policies")
  public ResponseEntity<Policy> addPolicy(@RequestBody Policy policy) {
    return ResponseEntity.ok().body(bosunService.addPolicy(policy));
  }

  @PatchMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<Policy> updatePolicy(@PathVariable String ciPolicyId,
      @RequestBody Policy policy) {
    return ResponseEntity.ok().body(bosunService.updatePolicy(policy));
  }
  
  @GetMapping(value = "/policies/violations")
  public ResponseEntity<List<PolicyViolations>> getViolations(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getViolations(ciTeamId));
  }

  @GetMapping(value = "/policies/insights")
  public ResponseEntity<List<PolicyInsights>> getInsights(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getInsights(ciTeamId));
  }


  @PostMapping(value = "/policies/validate")
  public ResponseEntity<PolicyActivityEntity> validatePolicy(@RequestBody PolicyValidation policyValidation) {
    return ResponseEntity.ok()
        .body(bosunService.validatePolicy(policyValidation));
  }
  
  @DeleteMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<PolicyResponse> deletePolicy(@PathVariable String ciPolicyId){  
    PolicyResponse response= bosunService.deletePolicy(ciPolicyId);
    
     return ResponseEntity.status(response.getStatus()).body(response);
  }
}
