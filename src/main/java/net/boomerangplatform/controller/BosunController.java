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

@RestController
@RequestMapping("/bosun")
public class BosunController {

  @Autowired
  private BosunService bosunService;

  @GetMapping(value = "/policies/operators")
  public ResponseEntity<Map<String, String>> getAllOperators() {
    return ResponseEntity.ok().body(bosunService.getAllOperators());
  }

  @GetMapping(value = "/policies")
  public ResponseEntity<List<Policy>> getPolicies(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().body(bosunService.getPoliciesByTeamId(teamId));
  }

  @GetMapping(value = "/policies/{policyId}")
  public ResponseEntity<Policy> getPolicy(@PathVariable String policyId) {
    return ResponseEntity.ok().body(bosunService.getPolicyById(policyId));
  }

  @PostMapping(value = "/policies")
  public ResponseEntity<Policy> addPolicy(@RequestBody Policy policy) {
    return ResponseEntity.ok().body(bosunService.addPolicy(policy));
  }

  @PatchMapping(value = "/policies/{policyId}")
  public ResponseEntity<Policy> updatePolicy(@PathVariable String policyId,
      @RequestBody Policy policy) {
    return ResponseEntity.ok().body(bosunService.updatePolicy(policy));
  }
  
  @GetMapping(value = "/policies/violations")
  public ResponseEntity<List<PolicyViolations>> getViolations(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().body(bosunService.getViolations(teamId));
  }

  @GetMapping(value = "/policies/insights")
  public ResponseEntity<List<PolicyInsights>> getInsights(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().body(bosunService.getInsights(teamId));
  }


  @PostMapping(value = "/policies/validate")
  public ResponseEntity<PolicyActivityEntity> validatePolicy(@RequestBody PolicyValidation policyValidation) {
    return ResponseEntity.ok()
        .body(bosunService.validatePolicy(policyValidation));
  }
  
  @DeleteMapping(value = "/policies/{policyId}")
  public ResponseEntity<PolicyResponse> deletePolicy(@PathVariable String policyId){  
    PolicyResponse response= bosunService.deletePolicy(policyId);
    
     return ResponseEntity.status(response.getStatus()).body(response);
  }
}
