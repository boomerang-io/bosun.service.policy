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
import net.boomerangplatform.model.PolicyTemplate;
import net.boomerangplatform.model.PolicyInsights;
import net.boomerangplatform.model.PolicyResponse;
import net.boomerangplatform.model.PolicyValidation;
import net.boomerangplatform.model.PolicyViolations;
import net.boomerangplatform.service.BosunService;

@RestController
@RequestMapping("/bosun/validate")
public class BosunValidationController {

  @Autowired
  private BosunService bosunService;

  @PostMapping(value = "")
  public ResponseEntity<PolicyActivityEntity> validatePolicy(@RequestBody PolicyValidation policyValidation) {
    return ResponseEntity.ok()
        .body(bosunService.validatePolicy(policyValidation));
  }
  
  @GetMapping(value = "/info/{policyId}")
  public ResponseEntity<PolicyValidation> validateInfo(@PathVariable String policyId) {
    return ResponseEntity.ok()
        .body(bosunService.validateInfo(policyId));
  }
  
}
