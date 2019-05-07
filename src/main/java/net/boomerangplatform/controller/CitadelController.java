package net.boomerangplatform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/citadel")
public class CitadelController {

  @GetMapping(value = "/teams")
  public ResponseEntity<?> getAllTeams() {
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies/definitions")
  public ResponseEntity<?> getAllDefinitions() {
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies/operators")
  public ResponseEntity<?> getAllOperators() {
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies")
  public ResponseEntity<?> getPolicies(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/policies/policies") // TODO: @RequestBody
  public ResponseEntity<?> addPolicy(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().build();
  }

  @PatchMapping(value = "/policies/policies") // TODO: @RequestBody
  public ResponseEntity<?> updatePolicy(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies/policies/violations")
  public ResponseEntity<?> getViolations(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/policies/policies/insights")
  public ResponseEntity<?> getInsights(
      @RequestParam(value = "teamId", required = true) String teamId) {
    return ResponseEntity.ok().build();
  }
}
