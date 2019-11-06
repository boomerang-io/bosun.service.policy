package net.boomerangplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.model.PolicyDefinition;
import net.boomerangplatform.service.BosunService;

@RestController
@RequestMapping("/bosun/definitions")
public class BosunDefinitionsController {

  @Autowired private BosunService bosunService;

  @GetMapping(value = "")
  public ResponseEntity<List<PolicyDefinition>> getAllDefinitions() {
    return ResponseEntity.ok().body(bosunService.getAllDefinitions());
  }

  @PostMapping(value = "")
  public ResponseEntity<PolicyDefinition> addDefinition(@RequestBody PolicyDefinition definition) {
    return ResponseEntity.ok().body(bosunService.addDefinition(definition));
  }
  
  @GetMapping(value = "/{definitionId}")
  public ResponseEntity<PolicyDefinition> getDefinition(@PathVariable String definitionId) {
    return ResponseEntity.ok().body(bosunService.getDefinition(definitionId));
  }
  
  @PatchMapping(value = "/{definitionId}")
  public ResponseEntity<PolicyDefinition> updateDefinition(@PathVariable String definitionId, @RequestBody PolicyDefinition definition) {
    return ResponseEntity.ok().body(bosunService.updateDefinition(definitionId, definition));
  }
}
