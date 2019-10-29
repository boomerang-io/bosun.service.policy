package net.boomerangplatform.repository.service;

import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;

public interface RepositoryService {
  DependencyGraph getDependencyGraph(String artifactPath, String artifactName,String artifactVersion);

  ArtifactSummary getArtifactSummary(String artifactPath, String artifactName,String artifactVersion);

  SonarQubeReport getSonarQubeReport(String id, String version);
  
  SonarQubeReport getSonarQubeTestCoverage(String id, String version);
}
