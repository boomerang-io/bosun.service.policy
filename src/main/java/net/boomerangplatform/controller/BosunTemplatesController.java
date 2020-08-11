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

import net.boomerangplatform.model.PolicyTemplate;
import net.boomerangplatform.service.BosunService;

@RestController
@RequestMapping("/policy/templates")
public class BosunTemplatesController {

  @Autowired private BosunService bosunService;

  @GetMapping(value = "")
  public ResponseEntity<List<PolicyTemplate>> getAllTemplates() {
    return ResponseEntity.ok().body(bosunService.getAllTemplates());
  }

  @PostMapping(value = "")
  public ResponseEntity<PolicyTemplate> addTemplate(@RequestBody PolicyTemplate template) {
    return ResponseEntity.ok().body(bosunService.addTemplate(template));
  }
  
  @GetMapping(value = "/{templateId}")
  public ResponseEntity<PolicyTemplate> getTemplate(@PathVariable String templateId) {
    return ResponseEntity.ok().body(bosunService.getTemplate(templateId));
  }
  
  @PatchMapping(value = "/{templateId}")
  public ResponseEntity<PolicyTemplate> updateTemplate(@PathVariable String templateId, @RequestBody PolicyTemplate template) {
    return ResponseEntity.ok().body(bosunService.updateTemplate(templateId, template));
  }
}
