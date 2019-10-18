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

import net.boomerangplatform.model.CiPolicy;
import net.boomerangplatform.model.CiPolicyDefinition;
import net.boomerangplatform.model.CiPolicyInsights;
import net.boomerangplatform.model.CiPolicyViolations;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.entity.CiPolicyActivityEntity;
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
  public ResponseEntity<List<CiPolicyDefinition>> getAllDefinitions() {
    return ResponseEntity.ok().body(bosunService.getAllDefinitions());
  }

  @GetMapping(value = "/policies/operators")
  public ResponseEntity<Map<String, String>> getAllOperators() {
    return ResponseEntity.ok().body(bosunService.getAllOperators());
  }

  @GetMapping(value = "/policies")
  public ResponseEntity<List<CiPolicy>> getPolicies(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getPoliciesByTeamId(ciTeamId));
  }

  @GetMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<CiPolicy> getPolicy(@PathVariable String ciPolicyId) {
    return ResponseEntity.ok().body(bosunService.getPolicyById(ciPolicyId));
  }

  @PostMapping(value = "/policies")
  public ResponseEntity<CiPolicy> addPolicy(@RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(bosunService.addPolicy(policy));
  }

  @PatchMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<CiPolicy> updatePolicy(@PathVariable String ciPolicyId,
      @RequestBody CiPolicy policy) {
    return ResponseEntity.ok().body(bosunService.updatePolicy(policy));
  }
  
  @GetMapping(value = "/policies/violations")
  public ResponseEntity<List<CiPolicyViolations>> getViolations(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getViolations(ciTeamId));
  }

  @GetMapping(value = "/policies/insights")
  public ResponseEntity<List<CiPolicyInsights>> getInsights(
      @RequestParam(value = "ciTeamId", required = true) String ciTeamId) {
    return ResponseEntity.ok().body(bosunService.getInsights(ciTeamId));
  }


  @GetMapping(value = "/policies/validate")
  public ResponseEntity<CiPolicyActivityEntity> validatePolicy(
      @RequestParam(value = "ciComponentActivityId", required = true) String ciComponentActivityId,
      @RequestParam(value = "ciComponentId", required = true) String ciComponentId,
      @RequestParam(value = "ciComponentVersion", required = true) String ciComponentVersion,
      @RequestParam(value = "ciPolicyId", required = true) String ciPolicyId) {
    return ResponseEntity.ok()
        .body(bosunService.validatePolicy(ciPolicyId, ciComponentActivityId, ciComponentId, ciComponentVersion));
  }
  
  @DeleteMapping(value = "/policies/{ciPolicyId}")
  public ResponseEntity<PolicyResponse> deletePolicy(@PathVariable String ciPolicyId){  
    PolicyResponse response= bosunService.deletePolicy(ciPolicyId);
    
     return ResponseEntity.status(response.getStatus()).body(response);
  }
}
