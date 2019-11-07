package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.service.BosunInternalService;

@RestController
@RequestMapping("/internal")
public class InternalController {

  @Autowired
  private BosunInternalService bosunInternalService;

  @GetMapping(value = "/bundle.tar.gz")
  public ResponseEntity<byte[]> getBundle() {
    return ResponseEntity.ok().header("Content-Type", "application/gzip").header("Content-Disposition", "attachment;filename=bundle.tar.gz").body(bosunInternalService.getBundle().toByteArray());
  }
  
}
