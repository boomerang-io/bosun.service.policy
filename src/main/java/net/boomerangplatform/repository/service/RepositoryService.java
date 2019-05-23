package net.boomerangplatform.repository.service;

import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;

public interface RepositoryService {
  DependencyGraph getDependencyGraph(String ciComponentId, String version);

  ArtifactSummary getArtifactSummary(String ciComponentId, String version);

  SonarQubeReport getSonarQubeReport(String ciComponentId, String version);
}
